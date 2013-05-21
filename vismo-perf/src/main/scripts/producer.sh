#!/bin/bash

prog=$(basename $0)
CONF=${CONF:-config.properties}
PORT=${PORT:-9991}

case $1 in
	send*)
		# topic/rate/no-events/size
		curl -v -X POST http://localhost:$PORT/events/$2/$3/$4/$5 ;;
	start*)
		exec java -cp "$PERF_JAR" gr.ntua.vision.monitoring.perf.Producer "$CONF" $PORT ;;
	halt*)
		curl -v -X POST http://localhost:$PORT/halt ;;
	*)
		echo "$prog: usage: $prog [start|send|halt]" 2>&1 ;
		exit 1 ;;
esac

exit 0
