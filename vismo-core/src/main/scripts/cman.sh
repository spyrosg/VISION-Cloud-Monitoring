#!/bin/bash

# cluster #1
cluster1="10.0.1.211,10.0.1.212,10.0.1.213"

# cluster #2
cluster2="10.0.1.214,10.0.1.215,10.0.1.216"

# cluster #3
cluster3="10.0.2.211,10.0.2.212,10.0.2.213"

# cluster #4
cluster4="10.0.2.214,10.0.2.215,10.0.2.216"

# cluster #5
cluster5="10.0.3.211,10.0.3.212,10.0.3.213"

# cluster 6
cluster6="10.0.3.214,10.0.3.215,10.0.3.216"

fab -k -u root -p oro-mv-aureo -H "$cluster2,$cluster3,$cluster6" -f cman.py "$@"
