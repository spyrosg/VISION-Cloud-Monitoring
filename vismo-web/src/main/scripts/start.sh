#!/bin/bash

JAR=vismo-web-1.0.7-SNAPSHOT.jar
CONFIG=vismo-web.yml
LOG=/var/log/vismo-web.log

setsid java -jar "$JAR" "$CONFIG" start <"$LOG" >&"$LOG" &
