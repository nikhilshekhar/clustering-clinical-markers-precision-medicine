# This Script Generates the training data and test data for
# PSM model. It computes the RMSE error on test data. 
# Author : Md S Q Zulkar Nine
# Date: July 24th, 2016

setwd("/Users/mds/Dropbox/gits/kidneyDataClustering/")
source("bsplines.R")
source("kernels.R")
source("psm.R")
source("prediction.R")
source("params.R")

library(dplyr)
library(readr)
library(caTools)
library(caret)



# date is in factor type: covert it into Date 
feature_matrix$timestamp = as.Date(feature_matrix$timestamp, format = "%Y-%m-%d")

# fm contains data without missing values:
feature_matrix_wo_missing_val <- na.omit(feature_matrix)

# convert the data into short form: 
# take out the pid, date, and gfr values


benchmark_raw <- subset(feature_matrix, select=c("pid","timestamp","LR_CR"))
benchmark <- na.omit(benchmark_raw)
benchmark$timestamp <- (as.numeric(format(benchmark$timestamp, "%Y%m%d")))


dataset_raw <- benchmark %>% group_by(pid) %>% do(t = .$timestamp, y = .$LR_CR)




# get the number of elements in each list
remove_less_than_ten <- function(x){
  flag = FALSE
  if (length(x) < 10){
    return(flag)
  }else{
    flag = TRUE
    return(flag)
  }
}

counts <- sapply(dataset_raw$y, remove_less_than_ten)
dataset <- dataset_raw[counts, ]

# save dataset2 for future use:
saveRDS(dataset, "filtered_dataset.rds")




# split the dataset 70% - 30% training and testing data.
# sample = sample.split(dataset2$pid, SplitRatio = .70)
# train_data = subset(dataset2, sample == TRUE)
# test_data = subset(dataset2, sample == FALSE)

# sample testing data:
test_data_sample <- sapply(dataset$t, get_test_index)


# Extracting testing data:
t_test_list <- mapply(extract_values, dataset$t, test_data_sample)
y_test_list <- mapply(extract_values, dataset$y, test_data_sample)
test_data_frame <- data.frame(dataset$pid,t_test_list,y_test_list)


# Remove test data from training data:
t_train_list <- mapply(remove_values, dataset$t, test_data_sample)
y_train_list <- mapply(remove_values, dataset$y, test_data_sample)



tt <- dataset$t
yt <- dataset$y

nobs <- vapply(t_train_list, length, numeric(1))
minobs <- vapply(y_train_list, min, numeric(1))
#valid <- minobs <= 2 & nobs > 2

fit <- run_em(t_train_list, y_train_list, opt)

# Save the model:
saveRDS(fit, "model.rds")

# Save the training data:
training_data <- list(
    dataset$pid
  , t_train_list
  , y_train_list
)
saveRDS(training_data, "training_data.rds")

# Save the test data:
test_data <- list(
    dataset$pid
  , t_test_list
  , y_test_list
)
saveRDS(test_data, "test_data.rds")


# Prediction part:
B <- lapply(tt, design, bb = bb) 

# compute the most likely group from Z. which could be W_zi.
membership <- apply(fit$Z, 2, which.max)

# Extract individual weights :
weight <- sapply(membership, function(x,y) y[,x], fit$W, simplify = FALSE)

#

results <- mapply( predictx, B, weight)

# Extract the test result:
test_result <- mapply(function(x,y) x[y], results, test_data_sample)

# Perform RMSE test:
rmse <- sqrt( sum( (y_test_list - test_result)^2 , na.rm = TRUE ) / length(y_test_list) )








