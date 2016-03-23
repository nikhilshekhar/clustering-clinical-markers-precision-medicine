
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


#NaN values


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

#Datframe is a list of values
for ( i in 1:nrow(findings_df)){
  finding_current_patient = findings_df[i,]
  colnames(finding_current_patient) <- c("pid","timestamp","testname","testval")
  #print(finding_current_patient)
  if (finding_current_patient$pid %in% list_of_patients){
    #print(finding_current_patient$pid)
    list_current_patients = subset(combined_df,combined_df$pid == finding_current_patient$pid)
    #print(list_current_patients)
    for(j in 1:nrow(list_current_patients)){
      #print(list_current_patients)                       #b = subset(findings_df,findings_df$pid == 8574069)
      #print(j)                                            #a = subset(combined_df,combined_df$pid == 8574069)
      current_pid = list_current_patients$pid
      current_timestamp = list_current_patients$timestamp
      list_current_finding = subset(findings_df,findings_df$pid == current_pid & findings_df$timestamp == current_timestamp)
      if (nrow(list_current_finding)!=0){
        print(list_current_patients)
        print(list_current_finding)
        for(field in names(combined_df)){
          #combined_df[[field]] = list_current_finding[[field]]
          print(combined_df[[field]])
        }
      }
      

    }
  }
}



