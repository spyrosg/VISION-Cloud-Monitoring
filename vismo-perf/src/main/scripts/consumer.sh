#!/bin/bash

CONF=${CONF:-config.properties}
PORT=${PORT:-9992}
prog=$(basename $0)

case $1 in
	list*)
		curl -v -X GET http://localhost:$PORT/handlers/ ;;
	no*)
		curl -v -X GET http://localhost:$PORT/handlers/"$2" ;;
	new*)
		curl -v -X PUT http://localhost:$PORT/handlers/"$2"/"$3" ;;
	reset*)
		curl -v -X POST http://localhost:$PORT/handlers/"$2" ;;
	start*)
		exec java -cp vismo-perf-*.jar gr.ntua.vision.monitoring.perf.Consumer "$CONF" $PORT >$2 & ;;
	halt*)
		curl -v -X POST http://localhost:$PORT/halt ;;
	*)
		echo "$prog: usage: $prog [start|list|no|new|reset|halt]" 2>&1 ;
		exit 1 ;;
esac

exit 0

