#!/usr/bin/env python
"""
Make violin plots from the data saved to android/assets/profilerLog.csv
"""

import random
import pandas
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

sns.set_style("whitegrid")

# load data
dataframe = pandas.read_csv('profilerLog.csv')
# trim crazy values
for row in dataframe:
    if (row != 'system'):
        dataframe = dataframe[dataframe[row] > 2E-45]
        dataframe = dataframe[dataframe[row] < 3E38]

ax = sns.violinplot(
    x="t_avg", y="system", data=dataframe,
    scale="width"
)
plt.savefig('t_avg.png', bbox_inches='tight')


ax = sns.violinplot(
    x="t_min", y="system", data=dataframe,
    scale="width"
)
plt.savefig('t_min.png', bbox_inches='tight')

ax = sns.violinplot(
    x="t_max", y="system", data=dataframe,
    scale="width"
)
plt.savefig('t_max.png', bbox_inches='tight')
