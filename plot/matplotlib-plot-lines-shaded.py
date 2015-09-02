#!/usr/bin/env python

import sys, os, shutil
import string, re
import matplotlib
import matplotlib.pyplot as plt
from matplotlib.backends.backend_pdf import PdfPages
matplotlib.rcParams['text.usetex'] = True

fileName = 'file-name.pdf'

tableau1  = (0.968, 0.714, 0.824)
tableau2  = (0.882, 0.478, 0.470)
tableau3  = (0.565, 0.663, 0.792)
tableau4  = (0.635, 0.635, 0.635)
tableau5  = (0.678, 0.545, 0.788)
tableau6  = (1.000, 0.620, 0.290)
tableau7  = (0.859, 0.859, 0.553)
tableau8  = (0.769, 0.612, 0.580)
tableau9  = (0.478, 0.757, 0.424)
tableau10 = (0.427, 0.800, 0.855)
tableau11 = (0.929, 0.592, 0.792)
tableau12 = (0.929, 0.400, 0.364)
tableau13 = (0.447, 0.620, 0.808)
tableau14 = (0.780, 0.780, 0.780)
tableau15 = (0.773, 0.690, 0.835)
tableau16 = (0.882, 0.616, 0.353)
tableau17 = (0.804, 0.800, 0.365)
tableau18 = (0.659, 0.471, 0.431)
tableau18 = (0.404, 0.749, 0.361)
tableau19 = (0.137, 0.122, 0.125)

nCPUList = [16, 32, 64, 128, 256, 512]
nCPUTickList = nCPUList

# aggregate event rate in events/s
allEventRates = [
    5484,
    10999,
    20652,
    36641,
    51114,
    75760
]

appEventRates = [
    278,
    547,
    1093,
    1822,
    2792,
    3831
]

avePayloadSizeInB = 256

megf = float(1024 * 1024)

# in MB/s
allEventsBW = map(
    lambda er: (float(er) * avePayloadSizeInB) / megf,
    allEventRates
)

# in MB/s
appEventsBW = map(
    lambda er: (float(er) * avePayloadSizeInB) / megf,
    appEventRates
)

def plotExperiment(nodes_list, dataTxRates, col, mark, lab):
    assert len(dataTxRates) == len(nodes_list)
    plt.plot(
        nodes_list,
        dataTxRates,
        '--',
        color=col,
        label = lab,
        linestyle = 'dashed',
        markersize = 10,
        marker = mark,
        markerfacecolor = col,
        linewidth = 2.0
    )

def make_plot(show = True, save = True):

    fig = plt.figure(figsize = (7, 5))

    plt.semilogx(basex = 2)

    plotExperiment(
        nCPUList,
        allEventsBW,
        tableau2,
        'D',
        'All Runtime Events'
    )
    plotExperiment(
        nCPUList,
        appEventsBW,
        tableau3,
        'v',
        'Task Invocation Events'
    )

    plt.fill_between(
        nCPUList,
        map(lambda x: x + 0.1, appEventsBW),
        map(lambda x: x - 0.1, allEventsBW),
        facecolor = tableau1,
        alpha = 0.4
    )

    plt.title(
        'Title',
        fontsize = 16
    )

    boxProps = dict(
        boxstyle = "round, pad = 0.3",
        fc = tableau1,
        ec = tableau1,
        lw = 1,
        alpha = 0.0
    )
    plt.text(
        256,
        5,
        "Our Bandwidth\nRequirements",
        ha = "center",
        va = "bottom",
        rotation = 0,
        size = 14,
        bbox = boxProps
    )

    plt.xlim(
        [min(nCPUList) - 2, max(nCPUList) + 48]
    )

    ySpace = 4
    plt.ylim(
        ymin = -1.0,
        ymax = max(
            max(allEventsBW), max(appEventsBW)
        ) + ySpace
    )

    l = plt.legend(loc = 2)
    plt.setp(l.get_title(), fontsize = 16)

    plt.xlabel('Job Size (Number of Processing Units)', fontsize = 16)
    plt.xticks(nCPUTickList, nCPUTickList)

    plt.ylabel(
        'Required Analysis Bandwidth (MB/s)',
        fontsize = 16
    )

    plt.grid(True)

    if show:
        plt.show()

    if save:
        print "Saving figure to " + fileName
        fig.savefig(
            fileName,
            format='pdf',
            bbox_inches='tight',
            pad_inches=0.1
        )

if __name__ == "__main__":
    make_plot(("-s" not in sys.argv), ("-w" in sys.argv))

