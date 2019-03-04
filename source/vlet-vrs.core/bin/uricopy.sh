#!/bin/bash 
##
# Project : VBrowser
# File    : uricopy.sh 
# Author  : P.T. de boer 
# Info    : 
#      Script interface to the Virtual File System (VFS) of the VBrowser.

##
#Usage: 
#
# uricopy.sh [-v] [-debug] -D<prop>=<value> [-move] [-force] <sourceVRL> <destinationVRL>
#
# Options:
#   -move            : delete source file or directory. 
#   -force           : always overwrite existing destination. 
#   -mkdirs          : create distination directory 
#   -result          : print resulting URI 
#   -v               : be verbose, specify twice for more. 
#   -debug           : print DEBUG information. 
#   -info            : print INFO  information 
#   
#   -D<prop>=<value> : set (java) system variable or (SRB) property

##
# settings 

##
# VLET_SYSCONFDIR 
# Set the following variable when the configuration files are NOT under $VLET_INSTALL/etc ! 
# This is automaticaly done when performing a binary installation 
#VLET_SYSCONFDIR=/etc/vlet

# bootstrap startup directory:
if [ -n  "$VLET_INSTALL" ] ; then 
	BASE_DIR=$VLET_INSTALL
else
	# Check basedir of installation. Assume script is started from $VLET_INSTALL/bin
	DIRNAME=`dirname $0`
	BASE_DIR=`cd $DIRNAME/.. ; pwd`
fi

# default sysconfdir, use BASE_DIR/etc 
if [ -z "$VLET_SYSCONFDIR" ] ; then 
	VLET_SYSCONFDIR=$BASE_DIR/etc 
fi

# source installation settings: 
if [ -f "$VLET_SYSCONFDIR/vletenv.sh" ] ; then 
   source $VLET_SYSCONFDIR/vletenv.sh 
else
   echo "*** Error: couldn't find vletenv.sh (Misconfigured VLET installation ?)."
   exit 1; 
fi

##
# java class to start
CLASS=nl.esciencecenter.vlet.vrs.tools.URICopy

# Start bootstrapper which does the rest 
$JAVA -Dvlet.install.sysconfdir=$VLET_SYSCONFDIR -jar $BASE_DIR/bin/bootstrapper.jar  $CLASS $@
# keep return value: 
RETVAL=$? 

#return exit code from VFSCopy
exit $RETVAL; 
