#!/bin/bash -x

export LANG=C
source vismo-perf/src/main/scripts/prep-perf.sh
prog=$(basename $0 | sed 's/.sh$//')

function run_perf {
	local no_events="$1"
	local event_size="$2"
	local rate="$3"
	local max_no_rules="$4"
	local results="$5"
	local topic="perf-var-rule-set"
	local tmp_out=perf.$$.tmp

	rm -f "$results"
	header_rules "$results"

	for no_rules in $(seq 100 50 "$max_no_rules")
	do
		start_vismo
		start_producer
		submit_rules $no_rules $topic
		warmup_vismo

		java -cp "$PERF_JAR" gr.ntua.vision.monitoring.perf.Consumer config.properties "$topic" "$event_size" "$no_events" >"$tmp_out" &
		cons_pid=$!
		sleep 3s

		generate_events "$topic" "$rate" "$no_events" "$event_size"
		wait $cons_pid

		record_round_rules "$tmp_out" "$results"
		sleep 1s

		stop_producer
		stop_vismo
	done

	rm -f "$tmp_out"
	return 0
}


set_config
run_perf "$@" $prog-$(date -Iseconds).csv
