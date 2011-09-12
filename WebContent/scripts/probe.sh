#!/bin/sh

ID="e0b3a92b-c860-4f8b-82f5-17114d0bcd48"
HOSTNAME=""
IP=""
START_TM=""
END_TM=""

LOAD_AVG=`cat /proc/loadavg | cut -f1 0d\ `
FREE_MEM=`free -k | head -2 | tail -1 |  tr -s /\  | cut -f4 -d\ `


echo "{"
echo "  \"probe\": \"$ID\","
echo "  \"source\": {"
echo "    \"host\": \"$HOSTNAME\","
echo "    \"address\": \"$IP\""
echo "  },"
echo "  \"type\": \"Measurement\","
echo "  \"start\": $START_TM,"
echo "  \"end\": $END_TM,"
echo "  \"resource\": ["
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
echo "}"

