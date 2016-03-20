# install.packages("RPostgreSQL")
require("RPostgreSQL")
 
# save the password that we can "hide" it as best as we can by collapsing it
my_password <- {
  "12"
}
# provide the database name :
database_name <- "kedneydata"

# provide server name:
server_name <- "localhost"

# provide the port number :
port_number = 5432

# provide the user name:
user_name = "postgres"

# create a connection
 
# loads the PostgreSQL driver
drv <- dbDriver("PostgreSQL")

# creates a connection to the postgres database
# note that "con" will be used later in each connection to the database
con <- dbConnect(drv, dbname = database_name,
                 host = server_name, port = port_number,
                 user = user_name, password = my_password)

# removes the password
rm(my_password) 
 
# check for the cartable
dbExistsTable(con, "cdr_gfr_derived")

# get everything from cdr_gfr_derived:
#gfr_table <- dbGetQuery(con,"select * from cdr_gfr_derived")

# get number of patients:
unique_patients <- dbGetQuery(con,"SELECT DISTINCT idperson As number_of_ids FROM cdr_gfr_derived")



# There are four tables, we are interested in: 
# cdr_gfr_derived
# cdr_history_social
# cdr_lab_results
# cdr_findings








