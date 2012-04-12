#!/bin/bash
# --------------------------------------------
# /etc/init.d/vision-vismo
# initd script for the VISION cloud monitoring component
# --------------------------------------------
# Source function library.
. /etc/rc.d/init.d/functions

case "$1" in
	start)
		echo -n "Starting vismo service: "
		RESULT=$(curl -s --anyauth -u vision:vision http://localhost:8080/manager/text/start?path=/vismo)
		echo $RESULT
		logger -t vision-vismo "Starting vismo service: $RESULT"
	;;
	stop)
		echo -n "Stopping vismo service: "
		RESULT=$(curl -s --anyauth -u vision:vision http://localhost:8080/manager/text/stop?path=/vismo)
		echo $RESULT
        logger -t vision-vismo "Stopping vismo service: $RESULT"
	;;
	status)
		echo -n "Getting status of vismo service: "
		RESULT=$(curl -s --anyauth -u vision:vision http://localhost:8080/manager/text/list?path=/vismo | grep vismo | awk 'BEGIN{ FS = ":" }{ print $2 }{}')
	    echo $RESULT
	    logger -t vision-vismo "Getting status of vismo service: $RESULT"
	;;
	restart)
		echo -n "Restaring vismo service: first stopping: "
		RESULT=$(curl -s --anyauth -u vision:vision http://localhost:8080/manager/text/stop?path=/vismo)
		echo $RESULT
	    logger -t vision-vismo "Restaring vismo service, stop result: $RESULT"
		echo -n "Restaring vismo service: then restarting: "
		RESULT=$(curl -s --anyauth -u vision:vision http://localhost:8080/manager/text/start?path=/vismo)
		echo $RESULT
	    logger -t vision-vismo "Restaring vismo service, then restarting: $RESULT"
	;;
	reload)
		echo -n "Reloading vismo service: "
		export RESULT=$(curl -s --anyauth -u vision:vision http://localhost:8080/manager/text/reload?path=/vismo)
		echo $RESULT
	    logger -t vision-vismo "Reloading vismo service: $RESULT"
	;;
	probe)
		echo -n "(probe not supported at the moment)"
	;;
	*)
		echo "Usage: vision-vismo {start|stop|status|reload|restart[|probe]"
		exit 1
	;;
esac

