#!/bin/sh

stats=`cat /proc/loadavg`

echo "load.`hostname`.last1min=`echo $stats | cut -f1 -d\ `"
echo "load.`hostname`.last5min=`echo $stats | cut -f2 -d\ `"
echo "load.`hostname`.last15min=`echo $stats | cut -f3 -d\ `"
