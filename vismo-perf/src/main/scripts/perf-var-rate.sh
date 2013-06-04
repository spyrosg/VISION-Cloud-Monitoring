#!/bin/bash -x

export LANG=C
source vismo-perf/src/main/scripts/prep-perf.sh
prog=$(basename $0 | sed 's/.sh$//')

function run_perf {
	local no_events="$1"
	local event_size="$2"
	local max_rate="$3"
	local results="$4"
	local topic="perf-var-rate"
	local tmp_out=perf.$$.tmp

	rm -f "$results"
	echo -e "#event-size\tevent-rate\tno-events\tlatency-min\tlatency-mean\tlatency-stddev\tlatench-max\tthroughput-min\tthroughput-mean\tthroughput-stddev\tthroughput-max\tmem-used-before\tmem-used-after" >"$results"

	local mem_before=$(vismo_memory_used)
	vismo_gc
	local mem_after=($vismo_memory_used)

	echo -e "0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t$mem_before)\t$mem_after" >>"$results"

	for rate in $(seq 100 50 "$max_rate")
	do
		java -cp "$PERF_JAR" gr.ntua.vision.monitoring.perf.Consumer config.properties "$topic" "$event_size" "$no_events" >"$tmp_out" &
		cons_pid=$!
		sleep 3s

		generate_events "$topic" "$rate" "$no_events" "$event_size"
		wait $cons_pid

		mem_before=$(vismo_memory_used)
		vismo_gc
		mem_after=$(vismo_memory_used)

		local stat=$("$STAT" "$tmp_out")
		echo -e "$event_size\t$rate\t$no_events\t$stat\t$mem_before\t$mem_after" >>"$results"

		sleep 1s
	done

	rm -f "$tmp_out"
}

main "$@" $prog-$(date +'%F').csv
