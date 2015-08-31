#!/usr/bin/env python

# a bar plot with errorbars
import numpy as np
import matplotlib.pyplot as plt

fileName = 'file-name.pdf'

# Number of things we are plotting
N = 6

eventRates = (
    5484,
    10999,
    20652,
    36641,
    51114,
    71581
)

# the x locations for the groups
ind = np.arange(N)  # the x locations for the groups
# the width of the bars
width = .9
# x offset
offset = 0.175

fig, ax = plt.subplots()

# calculate kilo events per second
keps = map (lambda ev: float(ev) / float(1000), eventRates)

rects1 = ax.bar(ind, keps, width, color= '#d76365')

# add some text for labels, title and axes ticks
ax.set_title(
    'Title'
)

ax.set_ylabel('Aggregate Event Rate (Kilo Events per Second)')
plt.yticks(fontsize = 16)
ax.set_ylim((0, max(keps) + 4))
ax.set_xlabel('Job Size (Number of Processing Units)')

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

autolabel(rects1)

fig.savefig(
    fileName,
    format = 'pdf',
    bbox_inches = 'tight',
    pad_inches = 0.1
)
