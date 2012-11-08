#!/bin/bash

source cloud-config.sh

fab -k -u root -p oro-mv-aureo -H "$test1,$test2,$vision1,$vision4,$vision5,$ibm" -f cman.py "$@"
