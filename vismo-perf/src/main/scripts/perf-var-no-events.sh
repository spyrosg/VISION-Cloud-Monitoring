#!/bin/bash -x

export LANG=C
source vismo-perf/src/main/scripts/prep-perf.sh
prog=$(basename $0 | sed 's/.sh$//')

function run_perf {
	local max_no_events="$1"
	local event_size="$2"
	local rate="$3"
	local results="$4"
	local topic="perf-var-no-events"
	local tmp_out=perf.$$.tmp

	rm -f "$results"
	header "$results"

	for no_events in $(seq 100 50 "$max_no_events")
	do
		java -cp "$PERF_JAR" gr.ntua.vision.monitoring.perf.Consumer config.properties "$topic" "$event_size" "$no_events" >"$tmp_out" &
		cons_pid=$!
		sleep 3s

		generate_events "$topic" "$rate" "$no_events" "$event_size"
		wait $cons_pid

		record_round "$tmp_out" "$results"
		sleep 1s
	done

	rm -f "$tmp_out"
}

main "$@" $prog-$(date -Iseconds).csv
