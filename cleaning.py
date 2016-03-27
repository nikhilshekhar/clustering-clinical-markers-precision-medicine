
# get the eGFR data
# egfr_df <- dbGetQuery(con,"SELECT * FROM cdr_gfr_derived")
# colnames(egfr_df) <- c("pid","timestamp","gender","birthyear","age","gfr","std_gfr")
egfr_df <- read.csv("~/PycharmProjects/Seminar702/dataset/cdr_gfr_derived.csv")
egfr_df[,"gfr"] <- NULL
colnames(egfr_df) <- c("pid","timestamp","gender","birthyear","age","gfr")

# Get the findings data
# findings_df <- dbGetQuery(con,"SELECT idperson,finddate,valuename,findvalnum FROM cdr_finding")
# colnames(findings_df) <- c("pid","timestamp","testname","testval")
findings_df <- read.csv("~/PycharmProjects/Seminar702/dataset/cdr_finding.csv")
keeps_findings_df <- c("idperson","finddate","valuename","findvalnum")
findings_df <- findings_df[keeps_findings_df]
colnames(findings_df) <- c("pid","timestamp","testname","testval")

#Get lab reports data
# lab_df <- dbGetQuery(con,"SELECT idperson,resultdate,valuename,resultvaluenum FROM cdr_lab_result")
# colnames(lab_df) <- c("pid","timestamp","testname","testval")
lab_df <- read.csv("~/PycharmProjects/Seminar702/dataset/cdr_lab_result.csv")
keeps_lab_df <- c("idperson","resultdate","valuename","resultvaluenum")
lab_df <- lab_df[keeps_lab_df]
colnames(lab_df) <- c("pid","timestamp","testname","testval")
# Make all lab tests values uppercase

#Normalize dates
egfr_df$timestamp = as.Date(egfr_df$timestamp,format="%Y-%m-%d")
findings_df$timestamp = as.Date(findings_df$timestamp,format ="%Y-%m-%d")
lab_df$timestamp = as.Date(lab_df$timestamp,format="%Y-%m-%d")
lab_df[,3] = toupper(lab_df[,3])

#Make index -->Not required in R

#List of patients who have eGFR reading
list_of_patients = unique(egfr_df$pid)   #63215

#make a new empty dataframe
unique_findings = levels(findings_df$testname)
unique_labtests = levels(lab_df$testname)
final_col_names = append(unique_findings,unique_labtests)

#Make a combined dataframe for all the feature vector
combined_df = egfr_df
for (newcol in final_col_names){
  combined_df[,newcol] <-NA
}

#For hadoop job
# write.csv(egfr_df,file = "egfr_df_from_r.csv")
# write.csv(findings_df,file = "findings_df_from_r.csv")
# write.csv(lab_df,file = "lab_df_from_r.csv")

#pid timestamp not there as columns in the python implementation
#total <- merge(combined_df, findings_df, by=c("pid","timestamp"))
#Populate the other fields by joining with other files

ii <- 0
jj <- 0
kk <- 0
#Datframe is a list of values
for ( i in 1:nrow(findings_df)){
  ii <- ii+1
  print("Rows read from findings_df:")
  print(ii)
  finding_current_patient = findings_df[i,]
  colnames(finding_current_patient) <- c("pid","timestamp","testname","testval")
#   print("finding_current_patient")
#   print(finding_current_patient)
  if (finding_current_patient$pid %in% list_of_patients){
    #print(finding_current_patient$pid)
    list_current_patients_from_combined = subset(combined_df,combined_df$pid == finding_current_patient$pid & combined_df$timestamp == finding_current_patient$timestamp)
    #list_current_patients_from_findings = subset(finding_current_patient,combined_df$pid == finding_current_patient$pid & combined_df$timestamp == finding_current_patient$timestamp)
    #print(list_current_patients)
    list_current_patients_from_findings = finding_current_patient
    if((length(list_current_patients_from_combined) > 0) & (length(list_current_patients_from_findings) > 0)){
      kk <- kk + 1
      print("Pid and timestamp join results more than one row")
    }
    
#     print(list_current_patients_from_combined)
#     print(list_current_patients_from_findings)
    for(j in 1:nrow(list_current_patients_from_combined)){
      #print(list_current_patients)                       #b = subset(findings_df,findings_df$pid == 8574069)
      #print(j)                                            #a = subset(combined_df,combined_df$pid == 8574069)
#       jj <- jj+1
#       print("Rows read from list_current_patients_from_combined:")
#       print(jj)
      current_pid_from_combined = list_current_patients_from_combined[j,]$pid
      current_timestamp_from_combined = list_current_patients_from_combined[j,]$timestamp
#       print(current_pid_from_combined)
#       print(current_timestamp_from_combined)
      list_current_finding = subset(list_current_patients_from_findings,list_current_patients_from_findings$pid == current_pid_from_combined & list_current_patients_from_findings$timestamp == current_timestamp_from_combined)
      if (nrow(list_current_finding)!=0){
        for(k in 1:nrow(list_current_finding)){
          print(list_current_finding[k,])
          for(field in names(combined_df)){
            #print(unique(list_current_finding[k,]$testname))
            if(field %in% unique(list_current_finding[k,]$testname)){
              value_from_findings = list_current_finding[k,]$testval
#               print("Value from findings:")
#               print(value_from_findings)
#               print("Field name:")
#               print(field)
              combined_df[field] = value_from_findings
              #current_combined_df = subset(combined_df,combined_df$pid == current_pid_from_combined & combined_df$timestamp == current_timestamp_from_combined)
              rownum = which(combined_df$pid == current_pid_from_combined & combined_df$timestamp == current_timestamp_from_combined)
              combined_df[rownum,][field] = value_from_findings
#               print("***********************")
#               print(combined_df[rownum,][field])
#               print("After adding")
#               print(combined_df[rownum,])
#               print("***********************")
               # combined_df[field] = value_from_findings
              #}
              
            
            
          }
        }
      }
    }
  }
  }
}



# Read the feature matrix 
feature_matrix <- read.csv("~/PycharmProjects/Seminar702/dataset/feature_matrix")

#Convert the nulls to NA
feature_matrix$FND_WTLB[which(feature_matrix$FND_WTLB == "null") ] <- NaN
feature_matrix$FND_BPS[which(feature_matrix$FND_BPS == "null") ] <- NaN
feature_matrix$FND_BPD[which(feature_matrix$FND_BPD == "null") ] <- NaN
feature_matrix$FND_HTIN[which(feature_matrix$FND_HTIN == "null") ] <- NaN
feature_matrix$LR_AST[which(feature_matrix$LR_AST == "null") ] <- NaN
feature_matrix$LR_MICROCR[which(feature_matrix$LR_MICROCR == "null") ] <- NaN
feature_matrix$LR_HDL[which(feature_matrix$LR_HDL == "null") ] <- NaN
feature_matrix$LR_TRIG[which(feature_matrix$LR_TRIG == "null") ] <- NaN

feature_matrix$LR_A1C[which(feature_matrix$LR_A1C == "null") ] <- NaN
feature_matrix$LR_CR[which(feature_matrix$LR_CR == "null") ] <- NaN
feature_matrix$LR_PTH[which(feature_matrix$LR_PTH == "null") ] <- NaN
feature_matrix$LR_GLUCNONFAST[which(feature_matrix$LR_GLUCNONFAST == "null") ] <- NaN
feature_matrix$LR_GLUCFAST[which(feature_matrix$LR_GLUCFAST == "null") ] <- NaN
feature_matrix$LR_LDL[which(feature_matrix$LR_LDL == "null") ] <- NaN
feature_matrix$LR_GFR[which(feature_matrix$LR_GFR == "null") ] <- NaN

feature_matrix$LR_VITD.25[which(feature_matrix$LR_VITD.25 == "null") ] <- NaN
feature_matrix$LR_ALT[which(feature_matrix$LR_ALT == "null") ] <- NaN
feature_matrix$LR_VITD.1_25[which(feature_matrix$LR_VITD.1_25 == "null") ] <- NaN
feature_matrix$LR_PHOS[which(feature_matrix$LR_PHOS == "null") ] <- NaN
feature_matrix$LR_GFR_AFRAMER[which(feature_matrix$LR_GFR_AFRAMER == "null") ] <- NaN

#Read the smoking history file
smoking_df <- read.csv("~/PycharmProjects/Seminar702/dataset/cdr_history_social.csv")
colnames(smoking_df) <- c("pid","smoker_status")

print(nrow(smoking_df))   #1059065
#Remove duplicate rows
smoking_df <- unique(smoking_df)
print(nrow(smoking_df))  #81184

#Add a column to the feature_matrix named "smoker_status"
#feature_matrix$smoking_status <- NA

#Join this to the feature matrix
feature_matrix <- merge(feature_matrix,smoking_df,by = "pid")

#Print the column names of the current columns
print(colnames(feature_matrix))

#Swap the efgr and smoker column
feature_matrix <- feature_matrix[,c(1,2,3,4,5,27,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,6)]

#Print the column named after swapping
print(colnames(feature_matrix))

#Count number of missing data in each column
na_count_per_column <-sapply(feature_matrix, function(y) sum(is.na(y)))
print(na_count_per_column)

#Total row count
row_count = nrow(feature_matrix)
print(row_count)

#Percentage of missing data
na_count_per_column <- (na_count_per_column/row_count)*100

#Threshold set: Features having missing data above the threshold are discarded
#Threshold set to : 85%

column_names = colnames(feature_matrix)

plot(column_names,head(na_count_per_column))

plot(na_count_per_column,type = "o")
require(ggplot2)
plot.ts(na_count_per_column)

write.csv(feature_matrix,file = "feature_matrix.csv")

threshold = 85
#Drop columns with missing data more than the threshold
for( i in 1:length(na_count_per_column)){
  if(na_count_per_column[i] > threshold){
    print("Column being dropped:")
    print(names(na_count_per_column[i]))
    feature_matrix[names(na_count_per_column[i])] <-NULL
  }
 
}

#Dimension of the matrix
dim(feature_matrix)

write.csv(feature_matrix,file = "feature_matrix_after_dropping_columns.csv")

#Removing outliers



