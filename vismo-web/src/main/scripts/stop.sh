#!/bin/bash

ps aux | awk '/vismo-web/ && !/awk/ { system("kill " $2 ); }'
sleep 2
