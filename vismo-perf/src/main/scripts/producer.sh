#!/bin/bash

CONF=${CONF:-config.properties}
PORT=${PORT:-9991}
prog=$(basename $0)

case $1 in
	start*)
		exec java -cp vismo-perf-*.jar gr.ntua.vision.monitoring.perf.Producer "$CONF" $PORT & ;;
	send*)
		curl -v -X POST http://localhost:$PORT/events/$2 ;;
	halt*)
		curl -v -X POST http://localhost:$PORT/halt ;;
	*)
		echo "$prog: usage: $prog [start|send|halt]" 2>&1 ;
		exit 1 ;;
esac

exit 0
