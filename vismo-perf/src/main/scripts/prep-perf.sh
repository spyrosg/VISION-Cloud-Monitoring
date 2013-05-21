#!/bin/bash -x

export LANG=C

VERSION=2.1.5-SNAPSHOT
TMP_DIR=/tmp/vismo-prep-perf.$$.tmp
PROD=vismo-perf/src/main/scripts/producer.sh
STAT=vismo-perf/src/main/scripts/csv-stat.py
PARSE=vismo-perf/src/main/scripts/parse.awk
VISMO_JAR=$(echo vismo-core/target/vismo-core-$VERSION.jar)
PERF_JAR=$(echo vismo-perf/target/vismo-perf-$VERSION.jar)

function start_producer {
	"$PROD" start >/dev/null 2>&1 &
	sleep 3s
}

function stop_producer {
	"$PROD" halt 2>/dev/null &
}

function start_vismo {
	nohup java -jar "$VISMO_JAR" config.properties start &
}

function stop_vismo {
	java -jar "$VISMO_JAR" config.properties stop
}

function generate_events {
	local topic="$1"
	local rate="$2"
	local no_events="$3"
	local event_size="$4"

	"$PROD" send "$topic" "$rate" "$no_events" "$event_size" 2>/dev/null
}

function warmup_vismo {
	local topic=warmup-topic
	local rate=1000.0
	local no_events=2000
	local event_size=1024
	local timeout=1m

	generate_events $topic $rate $no_events $event_size
	sleep $timeout
}

function main {
	start_vismo
	start_producer
	warmup_vismo

	run_perf "$@"

	stop_producer
	stop_vismo
	return 0
}
