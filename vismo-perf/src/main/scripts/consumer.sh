#!/bin/bash

prog=$(basename $0)
CONF=${CONF:-config.properties}

java -cp vismo-perf-*.jar gr.ntua.vision.monitoring.perf.Consumer "$CONF" "$@"

