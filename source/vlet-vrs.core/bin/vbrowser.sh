#!/bin/bash 
###
# (C) 2012-2013 Netherlands eScienceCenter
#
# file  : vbrowser.sh: 
# info  : VBrowser startup script. 
# author: P.T. de Boer
#

##
# settings 

##
# VLET_SYSCONFDIR 
# Set the following variable when the configuration files or NOT under $VLET_INSTALL/etc ! 
# This is automaticaly done when performing a binary installation 
#VLET_SYSCONFDIR=/etc/vlet

## 
# bootstrap startup directory:
DIRNAME=`dirname "$0"`
BASE_DIR=`cd "$DIRNAME"/.. ; pwd`
 

# VLET_SYSCONFDIR overrules VLET_INSTALL overrules BASE_DIR ! 
if [ -f "$VLET_SYSCONFDIR/etc/vletenv.sh" ] ; then
	source "$VLET_SYSCONFDIR/etc/vletenv.sh"
elif [ -f "$VLET_INSTALL/etc/vletenv.sh" ] ; then
	source "$VLET_INSTALL/etc/vletenv.sh"
elif [ -f "$BASE_DIR/etc/vletenv.sh" ] ; then
	source "$BASE_DIR/etc/vletenv.sh"
else
   echo "*** Warning: couldn't find VLET configuration file 'vletenv.sh' ..."
   # continue with defaults ? 
fi 

### Defaults: 

VBROWSER_JAR=${VBROWSER_JAR:-$BASE_DIR/bin/vbrowser-boot.jar}
JAVA=${JAVA:-java}

###
# Startup 

#default startup command line: currently none:

if [ -z "$@" ] ;then
  echo "No arguments"
  # start with userhome
  #OPTS="file://$HOME" 
  #if [ $cygwin == "true" ] ; then 
  #   OPTS="file://"`cygpath -m $HOME` 
  #fi
else
  OPTS="$@"
fi

# explicit exports

export VLET_INSTALL VLET_SYSCONFDIR 

###
# default classpath:
#

echo "VLET_INSTALL    ="$VLET_INSTALL 
echo "VLET_SYSCONFDIR ="$VLET_SYSCONFDIR 
echo "JAVA_HOME       ="$JAVA_HOME 
echo "CLASSPATH       ="$CLASSPATH
echo "VBROWSER_JAR    ="$VBROWSER_JAR
echo "Command line options ="$OPTS

# bootstrap class set's up real enviromment: 
echo "$JAVA" -cp $CLASSPATH -Dvlet.install.sysconfdir=$VLET_SYSCONFDIR -jar $VBROWSER_JAR $OPTS
"$JAVA" -cp "$CLASSPATH" -Dvlet.install.sysconfdir="$VLET_SYSCONFDIR" -jar "$VBROWSER_JAR" $OPTS

