import numpy as np
from numpy import concatenate, array
from numpy.random import randn
# Decimal precision value to display in the matrix
np.set_printoptions(precision=5, suppress=True)

# Scipy
import scipy
import scipy.stats as stats

# Matplotlib
import matplotlib.cm as cm
import matplotlib.dates as mdates
import matplotlib.pyplot as plt
import matplotlib.dates as mdates
#%matplotlib inline
#mpl.rc('figure', figsize=(10, 8))

# Pandas experiments
import pandas as pd
from pandas import Series, DataFrame, Panel

# Misc
import time
import datetime as dt
import math
import random
# import seaborn as sns
print 'All libraries loaded.'

# eGFR data
egfr_df = pd.read_csv('dataset/cdr_gfr_derived.csv', parse_dates=['resultdata'])
egfr_df.drop('gfr', axis=1, inplace=True)
egfr_df.columns = ['pid', 'timestamp', 'gender', 'birthyear', 'age', 'gfr']

# Findings data
findings_df = pd.read_csv('dataset/cdr_finding.csv', parse_dates=['finddate'], usecols=['idperson', 'finddate', 'valuename', 'findvalnum'])
findings_df = findings_df[['idperson', 'finddate', 'valuename', 'findvalnum']]
findings_df.columns = ['pid', 'timestamp', 'testname', 'testval']

# Lab reports data
lab_df = pd.read_csv('dataset/cdr_lab_result.csv', parse_dates=['resultdate'], usecols=['idperson', 'resultdate', 'valuename', 'resultvaluenum'])
lab_df = lab_df[['idperson', 'resultdate', 'valuename', 'resultvaluenum']]
lab_df.columns = ['pid', 'timestamp', 'testname', 'testval']
# Make all lab tests values uppercase
lab_df.testname = map(lambda x: x.upper(), lab_df.testname)




# Normalize dates (to remove the time part of it)
egfr_df.timestamp = egfr_df.timestamp.map(pd.datetools.normalize_date)
findings_df.timestamp = findings_df.timestamp.map(pd.datetools.normalize_date)
lab_df.timestamp = lab_df.timestamp.map(pd.datetools.normalize_date)



# NaN values
total_rowcount = len(lab_df.testval.values)
nan_rowcount = len([x for x in lab_df.testval.values if math.isnan(x)])
print '\n',(str(nan_rowcount*100/total_rowcount)+ "% of the values are NaN")

# Set the index as a combination of the person ID and timestamp
egfr_df.set_index(['pid', 'timestamp'], inplace=True)
findings_df.set_index(['pid', 'timestamp'], inplace=True)
lab_df.set_index(['pid', 'timestamp'], inplace=True)


list_of_patients = list(set(egfr_df.index.get_level_values('pid').values))
print '\nFound', len(list_of_patients), 'unique patients'


# Column names will be a combination of all the lab test names and finding names
unique_findings = set(findings_df.testname.values)
unique_labtests = set(lab_df.testname.values)
print '\nFindings:', list(unique_findings)
print 'Lab tests:', list(unique_labtests)
final_col_names = np.append(list(unique_findings), list(unique_labtests))

# Row locations in the eGFR dataframe of patients
# locations_of_patients = []
# for patient in list_of_patients:
#     get_loc_result = egfr_df.index.get_loc(patient)
#     locations_of_patients += [i for i,v in enumerate(get_loc_result) if v == True]

# combined_df = DataFrame(egfr_df.iloc[locations_of_patients])
combined_df = pd.DataFrame(egfr_df)


# Add the new columns
for newcol in final_col_names:
    combined_df[newcol] = np.nan




