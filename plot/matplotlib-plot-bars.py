#!/usr/bin/env python

# a bar plot with errorbars
import numpy as np
import matplotlib.pyplot as plt
import matplotlib
matplotlib.rcParams['text.usetex'] = True

# colors
tableau2 = (0.882,0.478,0.470)
tableau3 = (0.565,0.663,0.792)

fileName = 'file-name.pdf'

# Number of things we are plotting
N = 6

# includes all legion analysis events (events per second)
allEventRates = (
    5484,
    10999,
    20652,
    36641,
    51114,
    75760
)

# just app-related events (events per second)
appEventRates = (
    278,
    547,
    1093,
    1822,
    2792,
    3831
)

ind = np.arange(N)  # the x locations for the groups
# the width of the bars
width = .90
offset = 0.175

fig, ax = plt.subplots()
# kilo events per second
allKEPS = map(lambda ev: float(ev) / float(1000), allEventRates)
appKEPS = map(lambda ev: float(ev) / float(1000), appEventRates)

rects1 = ax.bar(ind, allKEPS, width, color = tableau2)
rects2 = ax.bar(ind, appKEPS, width, color = tableau3)


# add some text for labels, title and axes ticks
ax.set_title(
    'Title',
    fontsize = 16
)

ax.set_ylabel(
    'Average Aggregate Event Rate (Kilo Events per Second)',
    fontsize = 16
)
plt.yticks(fontsize = 16)
ax.set_ylim((0, max(allKEPS) + 4))
ax.set_xlabel('Job Size (Number of Processing Units)', fontsize = 16)

ax.set_xticks(ind + (width / 2))
plt.xlim([0, ind.size])

ax.set_xticklabels(
    ('16', '32', '64', '128', '256', '512'),
    fontsize = 16
)

def autolabel(rects):
    for rect in rects:
        height = rect.get_height()
        plt.text(
            rect.get_x() + rect.get_width() / 2.,
            height + 0.1,
            '%.2f'%height,
            ha = 'center',
            va='bottom'
        )

ax.legend(
    (rects1[0], rects2[0]),
    ('All Runtime Events',
     'Task Invocation Events'),
    loc = 2
)

autolabel(rects1)
autolabel(rects2)

print "Saving figure to " + fileName
fig.savefig(
    fileName,
    format = 'pdf',
    bbox_inches = 'tight',
    pad_inches = 0.1
)
