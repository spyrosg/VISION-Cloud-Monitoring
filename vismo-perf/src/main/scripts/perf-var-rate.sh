#!/bin/bash -x

export LANG=C
source vismo-perf/src/main/scripts/prep-perf.sh

function run_perf {
	local no_events="$1"
	local event_size="$2"
	local max_rate="$3"
	local results="$4"
	local topic="perf-var-rate"
	local tmp_out=perf.$$.tmp

	rm -f "$results"
	echo "# event-size, event-rate, no-events, latency-mean, throughput-mean" >"$results"

	for rate in $(seq 100 50 "$max_rate")
	do
		java -cp vismo-perf-*.jar gr.ntua.vision.monitoring.perf.Consumer config.properties "$topic" "$event_size" "$no_events" >"$tmp_out" &
		cons_pid=$!
		sleep 3s

		generate_events "$topic" "$rate" "$no_events" "$event_size"
		wait $cons_pid

		echo -n $event_size,$rate,$no_events, >>"$results"
		"$STAT" "$tmp_out" | "$PARSE" >>"$results"
		sleep 5s
	done

	rm -f "$tmp_out"
}

main "$@" perf-var-rate-results-$(date +'%F').csv
