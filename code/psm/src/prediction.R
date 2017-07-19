# This script is for prediction of PSM model:

# Functions to get the test data.
get_test_index <- function(x){
  l <- length(x)
  ind <- sample(1:l, 1)
  return(ind)
}

extract_values <- function(x, index_val ){
  val <- x[index_val]
  return(val)
}

remove_values <- function(x, index_val){
  val <- x[-index_val]
  return(val)
}

predictx <- function(d, w){
  result <- d %*% w
  return(result)
}

predict_test_data <- function(t, y, Z, W, kfns ){
  # params:
  # t - whole thing - test + train
  # y - whole thing - test + train
  
  B <- lapply(t, design, bb = bb) 
  
  # compute the most likely group from Z. which could be W_zi.
  membership <- apply(fit$Z, 2, which.max)
  
  # Extract individual weights :
  weight <- sapply(membership, function(x,y) y[,x], fit$W, simplify = FALSE)
  
  #
  
  
  results <- mapply( predictx, B, weight)
  #result <- sapply(B, predict, , )
  
  
}