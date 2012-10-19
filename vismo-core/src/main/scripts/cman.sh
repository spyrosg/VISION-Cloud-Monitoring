#!/bin/bash

source cloud-config.sh

fab -k -u root -p oro-mv-aureo -H "$vision1,$vision4,$vision5,$ibm" -f cman.py "$@"
