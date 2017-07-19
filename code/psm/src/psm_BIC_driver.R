# This Script is to do PSM with our dataset : 
# To do this we will remove all the na's from the feature matrix:
# Author : Md S Q Zulkar Nine
# Date: August 4th, 2016

setwd("/Users/mds/Dropbox/gits/kidneyDataClustering/")
source("bsplines.R")
source("kernels.R")
source("psm.R")

library(dplyr)
library(readr)
library(caTools)
library(caret)

set.seed(20150610)

bb <- bspline_basis(19680000, 20230000, 5, 2)

opt <- list(
  num_subtypes = 9     ## How many subpopulations to use.
  , bb           = bb    ## The B-spline basis object.
  , noise_v      = 1.0   ## The random noise variance.
  , const_v      = 16.0  ## The magnitude of the long-term kernel component.
  , ou_v         = 64.0  ## The magnitude of the short-term kernel component.
  , ou_l         = 4.0   ## The length-scale of the short-term kernel component.
  , maxiter      = 100   ## The max. number of EM iterations to run.
  , tol          = 1e-4  ## The relative improvement threshold to use as a stopping point.
)
# Control variable:
load("~/Dropbox/gits/data/CSE702/feature_matrix.rda")

# date is in factor type: covert it into Date 
feature_matrix$timestamp = as.Date(feature_matrix$timestamp, format = "%Y-%m-%d")

# fm contains data without missing values:
data_bank <- na.omit(feature_matrix)

# convert the data into short form: 
# take out the pid, date, and gfr values


benchmark1 <- subset(feature_matrix, select=c("pid","timestamp","LR_CR"))
benchmark1 <- na.omit(benchmark1)
benchmark1$timestamp <- (as.numeric(format(benchmark1$timestamp, "%Y%m%d")))


dataset1 <- benchmark1 %>% group_by(pid) %>% do(t = .$timestamp, y = .$LR_CR)




# get the number of elements in each list
remove_less_than_five <- function(x){
  flag = FALSE
  if (length(x) < 10){
    return(flag)
  }else{
    flag = TRUE
    return(flag)
  }
}

counts <- sapply(dataset1$y, remove_less_than_five)
dataset2 <- dataset1[counts, ]

# save dataset2 for future use:
saveRDS(dataset2, "filtered_dataset.rds")

# split the dataset 70% - 30% training and testing data.
sample = sample.split(dataset2$pid, SplitRatio = .70)
train_data = subset(dataset2, sample == TRUE)
test_data = subset(dataset2, sample == FALSE)

# k = 5 fold data from train_data :
#folds <- createFolds(train_data$pid, k = 5, list=TRUE, returnTrain = FALSE)



# function to compute BIC:
compute_BIC <- function(folds, opt, train_data){
  data <- train_data[folds,]
  tt <- data$t
  yt <- data$y
  
  fit <- run_em(tt, yt, opt)
  
  bic_val <- ( -2 * fit$logl ) + 3* log(length(tt), base = exp(1))
  return(bic_val)
}

# Compute BIC:
#bic_list <- sapply(folds, compute_BIC, opt=opt, train_data = train_data, simplify=TRUE)

# # Compute BIC for each G:
bic_main <- 3:15
for (i in 3:15){
  
  # change number of subtypes in opt
  opt$num_subtypes = i
  
  # k = 5 fold data from train_data :
  folds <- createFolds(train_data$pid, k = 5, list=TRUE, returnTrain = FALSE)
  
  # Compute BIC:
  bic_list <- sapply(folds, compute_BIC, opt=opt, train_data = train_data, simplify=TRUE)
  
  bic_main[i] <- mean(bic_list)
  
  print(paste0("completed - ",toString(i)))

}

# Curve Generation:
plot(3:15,bic_main[3:15],type="o", main="BIC Curve", ylab="Average BIC across five folds",xlab="Number of Clusters" )
