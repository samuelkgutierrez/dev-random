#!/bin/tcsh
# -------v change v ------------------------------------------------------------
#MSUB -N JOB.NAME
#MSUB -j oe

# number of nodes
set num_nodes = $PBS_NUM_NODES
# procs per node
set ppn       = $PBS_NUM_PPN
# total number of processes
@ numpe       = ($num_nodes * $ppn)
################################################################################
# base of job directory structure
set job_base  = "/tmp"
# top-level name of tests that shall be run
set job_name  = $PBS_JOBNAME
#
set job_home  = "${job_base}/${job_name}"
# job sizes that we want run in this script
set job_sizes = (1 2 4 8 16)
# number of job trials
set num_trials = 4
# 2 job types (names)
set job_types = (type1 type2)

echo 'Starting Job ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'
echo "Start: `date`"
echo
echo 'Job Summary :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'
echo "Job Name  : $job_name"
echo "Job Home  : $job_home"
echo "# Nodes   : $num_nodes"
echo "PPN       : $ppn"
echo "NUMPE     : $numpe"
echo
echo 'Modules'
#module list
echo
echo ':::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'
echo
echo 'Building top-level directory structure...'
mkdir -p ${job_home}
cd ${job_home}
echo "PWD: `pwd`"
echo
foreach n ($job_sizes)
    foreach t (`seq 1 $num_trials`)
        mkdir -p "${job_home}/${n}PE/${t}"
        pushd "${n}PE/${t}" >& /dev/null
        #
        mkdir $job_types[1]
        pushd $job_types[1] >& /dev/null
        echo "Type1 in `pwd`"
        popd >& /dev/null
        #
        mkdir $job_types[2]
        pushd $job_types[2] >& /dev/null
        echo "Type2 in `pwd`"
        popd >& /dev/null
        #
        popd >& /dev/null
    end
end
echo
echo "End: `date`"
echo ':::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::'
