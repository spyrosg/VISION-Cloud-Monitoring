#!/bin/sh

ID="e0b3a92b-c860-4f8b-82f5-17114d0bcd48"
HOSTNAME=`hostname`
IP=`hostname -i`
START_TM=`date +%s`
START_TM=$(($START_TM * 1000))
END_TM=$START_TM

LOAD_AVG=`cat /proc/loadavg | cut -f1 -d\ `
FREE_MEM=`free -k | head -2 | tail -1 |  tr -s /\  | cut -f4 -d\ `


echo "[{"
echo "  \"probe\": \"$ID\","
echo "  \"source\": {"
echo "    \"host\": \"$HOSTNAME\","
echo "    \"address\": \"$IP\""
echo "  },"
echo "  \"type\": \"Measurement\","
echo "  \"start\": $START_TM,"
echo "  \"end\": $END_TM,"
echo "  \"resources\": ["
echo "    {"
echo "      \"unit\": \"-\","
echo "      \"value\": $LOAD_AVG,"
echo "      \"type\": \"LoadAverage\""
echo "    },"
echo "    {"
echo "      \"unit\": \"KB\","
echo "      \"value\": $FREE_MEM,"
echo "      \"type\": \"Memory\""
echo "    }"
echo "  ]"
echo "}]"

