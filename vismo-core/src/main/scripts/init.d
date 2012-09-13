#!/bin/bash
# --------------------------------------------
# /etc/init.d/vision-vismo
# initd script for the VISION cloud monitoring component
# --------------------------------------------
# Source function library.
. /etc/rc.d/init.d/functions

VISMO_JAR=/srv/vismo/vismo.jar
VISMO_CONFIG=/srv/vismo/config.properties


is_vismo_running() {
	java -jar "$VISMO_JAR" "$VISMO_CONFIG" status 2>&1 | grep -q '[0-9]$' 2>/dev/null
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
	java -jar "$VISMO_JAR" "$VISMO_CONFIG" stop >/dev/null

	if ! `is_vismo_running`; then
		logger -t vision-vismo "Stopping vismo service: ok"
		return 0
	else
		logger -t vision-vismo "Stopping vismo service: failed"
		return 1
	fi
}


vismo_status() {
	java -jar "$VISMO_JAR" "$VISMO_CONFIG" status >/dev/null
	return 0
}


vismo_restart() {
	vismo_stop && sleep 1 && vismo_start
}


vismo_probe() {
	echo "vismo: probe not supported"
	return 0
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
