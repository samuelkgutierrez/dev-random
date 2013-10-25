#!/bin/tcsh

# author: samuel k. gutierrez

# jr - job retry script

#MOAB -N test-name

set nonomatch

################################################################################
# user setup - change here ###
################################################################################
# job name
setenv PBS_JOBNAME "test-name.2"
# the base directory where jr will write its files
setenv JR_TMPDIR_BASE $HOME
# your job's name 
setenv JR_JOB_NAME $PBS_JOBNAME
# max # retries
setenv JR_RETRY_MAX 3
# jr tmp file directory
setenv JR_TMPDIR_NAME ".jr-tmp"
################################################################################
# end user setup - change here ###
################################################################################
# full tmpdir path
setenv JR_TMPDIR "$JR_TMPDIR_BASE/$JR_TMPDIR_NAME"

echo "### starting job under job retry harness..."
echo "### job name: $JR_JOB_NAME"
echo "### max retries: $JR_RETRY_MAX"
echo "### user: $USER"
# init setup
echo "ooo check init setup required..."
if (! -d "$JR_TMPDIR") then
    echo "   $JR_TMPDIR not found. creating it for you..."
    mkdir -p "$JR_TMPDIR"
    if (0 != $?) then
        echo "could not create jr tmpdir... bye!"
        exit 1
    endif
else
    echo "   $JR_TMPDIR found. using it..."
endif
# now look for job resubmit info for this job series
echo "ooo looking for resubmission info in $JR_TMPDIR"
set jr_jobmeta = "$JR_TMPDIR/$JR_JOB_NAME"
# file format: JR_TMPDIR/PBS_JOBNAME.RETRY#
if (-e $jr_jobmeta) then
    echo "   found one for this job series."
    @ cret = `cat $jr_jobmeta`
    @ nret =  $cret + 1
    echo $nret > $jr_jobmeta
else
    echo "   none found for this job series. creating one..."
    echo 0 > $jr_jobmeta
    if (0 != $?) then
        echo "WARNING: could not create retry metadata..."
    endif
endif
@ cret = `cat $jr_jobmeta`
@ maxr = `echo $JR_RETRY_MAX`
if ($cret > $maxr) then
    echo "WARNING: max retries exceeded for this job series... exiting"
    exit 1
else
    echo "### parallel job start..."
    echo "your mpirun goes here..."
    if (0 != $?) then
        echo "### FAILURE DETECTED... resubmitting..."
    else
        # success! cleanup
        echo "success!"
        rm $jr_jobmeta
    echo "### parallel job end..."
endif
echo "### done with job under job retry harness..."
