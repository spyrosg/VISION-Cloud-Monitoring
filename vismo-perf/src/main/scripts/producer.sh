#!/bin/bash

CONF=${CONF:-config.properties}
PORT=${PORT:-9991}
prog=$(basename $0)

case $1 in
	send*)
		curl -v -X POST http://localhost:$PORT/events/$2/$3/$4 ;;
	start*)
		exec java -cp vismo-perf-*.jar gr.ntua.vision.monitoring.perf.Producer "$CONF" $PORT & ;;
	halt*)
		curl -v -X POST http://localhost:$PORT/halt ;;
	*)
		echo "$prog: usage: $prog [start|send|halt]" 2>&1 ;
		exit 1 ;;
esac

exit 0
