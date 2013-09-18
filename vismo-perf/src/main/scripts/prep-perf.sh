#!/bin/bash -x

export LANG=C

VERSION=$(awk '/version>/ { gsub(/<\/?version>/, ""); print $1; exit 0 }' pom.xml)
TMP_DIR=/tmp/vismo-prep-perf.$$.tmp
PROD=vismo-perf/src/main/scripts/producer.sh
STAT=vismo-perf/src/main/scripts/csv-stat.py
CONF=vismo-perf/src/test/resources/config.properties

export VISMO_JAR=$(echo vismo-core/target/vismo-core-$VERSION.jar)
export PERF_JAR=$(echo vismo-perf/target/vismo-perf-$VERSION.jar)

function set_config {
	local my_ip=$(/sbin/ifconfig -a | awk '/inet\ / { sub(/addr:/, ""); print $2; exit 0 }')

	sed 's/cluster.head = .*$/cluster.head = '$my_ip'/' $CONF >config.properties
	rm -fr vismo.log
}

function start_producer {
	"$PROD" start >/dev/null 2>&1 &
	sleep 3s
}

function stop_producer {
	"$PROD" halt 2>/dev/null &
}

function start_vismo {
	nohup java -jar "$VISMO_JAR" config.properties start >>vismo.log 2>&1 &
	sleep 3
	java -jar "$VISMO_JAR" config.properties status || exit 1
}

function stop_vismo {
	java -jar "$VISMO_JAR" config.properties stop >/dev/null 2>&1
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

function vismo_memory_used {
	echo $(curl -X POST http://localhost:9996/mon/mem 2>/dev/null | vismo-perf/src/main/scripts/mem.awk)
}

function vismo_gc {
	curl -X POST http://localhost:9996/mon/gc >/dev/null 2>&1
}

function submit_rules {
	local i

	for i in $(seq $1)
	do
		curl -s -X POST http://localhost:9996/rules \
			-H 'Content-Type: application/json' \
			-d '{ "topic": "'$2'", "filterUnit": null, "operation": "GET", "requirements": [ { "metric": "latency", "predicate": ">", "threshold": 1.3 } ] }'
	done
}

function header {
	local results="$1"

	echo -e "#event-size\tevent-rate\tno-events\tlatency-min\tlatency-mean\tlatency-stddev\tlatench-max\tthroughput-min\tthroughput-mean\tthroughput-stddev\tthroughput-max\tmem-used-before\tmem-used-after" >"$results"
	local mem_before=$(vismo_memory_used)
	vismo_gc
	local mem_after=$(vismo_memory_used)
	echo -e "0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t$mem_before\t$mem_after" >>"$results"
}

function record_round {
	local tmp_file="$1"
	local results="$2"

	local mem_before=$(vismo_memory_used)
	vismo_gc
	local mem_after=$(vismo_memory_used)

	local stat=$("$STAT" "$tmp_file")
	echo -e "$event_size\t$rate\t$no_events\t$stat\t$mem_before\t$mem_after" >>"$results"
}

function header_rules {
	local results="$1"

	echo -e "#no-rules\tevent-size\tevent-rate\tno-events\tlatency-min\tlatency-mean\tlatency-stddev\tlatench-max\tthroughput-min\tthroughput-mean\tthroughput-stddev\tthroughput-max\tmem-used-before\tmem-used-after" >"$results"
	local mem_before=$(vismo_memory_used)
	vismo_gc
	local mem_after=$(vismo_memory_used)
	echo -e "0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t$mem_before\t$mem_after" >>"$results"
}

function record_round_rules {
	local tmp_file="$1"
	local results="$2"

	local mem_before=$(vismo_memory_used)
	vismo_gc
	local mem_after=$(vismo_memory_used)

	local stat=$("$STAT" "$tmp_file")
	echo -e "$no_rules\t$event_size\t$rate\t$no_events\t$stat\t$mem_before\t$mem_after" >>"$results"
}

function main {
	set_config
	start_vismo
	start_producer
	warmup_vismo

	run_perf "$@"

	stop_producer
	stop_vismo
	rm -f config.properties
	return 0
}
