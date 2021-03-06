#!/bin/bash
# --------------------------------------------
# /etc/init.d/vision-vismo
# initd script for the VISION cloud monitoring component
# --------------------------------------------
# Source function library.
. /etc/rc.d/init.d/functions

VISMO_JAR=/srv/vismo/vismo.jar
VISMO_CONFIG=/etc/visioncloud_vismo.conf


is_vismo_running() {
	java -jar "$VISMO_JAR" "$VISMO_CONFIG" status | grep -q 'pid: [0-9]\+'
}


vismo_start() {
	echo -n "vismo: "
	setsid java -jar "$VISMO_JAR" "$VISMO_CONFIG" start </dev/null >&/dev/null &
        sleep 1

	if `is_vismo_running`; then
		echo started
		logger -t vision-vismo "Starting vismo service: ok"
		return 0
	else
		echo failed
		logger -t vision-vismo "Starting vismo service: failed"
		return 1
	fi
}


vismo_stop() {
	java -jar "$VISMO_JAR" "$VISMO_CONFIG" stop
	sleep 1

	if ! `is_vismo_running`; then
		logger -t vision-vismo "Stopping vismo service: ok"
		return 0
	else
		logger -t vision-vismo "Stopping vismo service: failed"
		return 1
	fi
}


vismo_status() {
	java -jar "$VISMO_JAR" "$VISMO_CONFIG" status
	return 0
}


vismo_restart() {
	vismo_stop && sleep 1 && vismo_start
}


vismo_probe() {
	echo "vismo: probe not supported"
	return 1
}


case "$1" in
	start) vismo_start && exit 0 || exit 1 ;;
	stop) vismo_stop && exit 0 || exit 1 ;;
	restart) vismo_restart ;;
	status) vismo_status ;;
	probe) vismo_probe ;;
	*) echo "Usage: vision-vismo {start|stop|status|restart[|probe]}"; exit 1 ;;
esac

# make sure we always end with a clean status
exit 0
