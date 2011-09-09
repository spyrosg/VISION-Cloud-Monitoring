#!/bin/sh

stats=`free -k | head -2 | tail -1 |  tr -s /\  `

echo "memory.`hostname`.total=`echo $stats | cut -f2 -d\ `k"
echo "memory.`hostname`.free=`echo $stats | cut -f4 -d\ `k"
