
# load the model file :
fit <- readRDS(file = "model.rds")

# load the filtered dataset as well: 
dataset <- readRDS("filtered_dataset.rds")

# get the members of each group:
membership_matrix <- fit$Z

#rownames(membership_matrix) <- c("G1","G2","G3","G4","G5","G6","G7", "G8", "G9")

# get which patient is in which group:
member_list <- sapply(1:ncol(membership_matrix), function(x) {which.max(membership_matrix[,x])} )

membership_dictionary <- cbind(dataset$pid, member_list)

member_dict <- data.frame(dataset$pid, member_list)
colnames(member_dict) <- c("pid", "group_name")

grouped <- membership_dictionary %>% group_by(group_name) %>% do(pid = .$pid, grps = .$group_name)

group1 <- grouped$pid[1]
group1 <- group1[[1]]

# get the group1 data:
grp1_data <- dataset[dataset$pid == group1[1], ]
