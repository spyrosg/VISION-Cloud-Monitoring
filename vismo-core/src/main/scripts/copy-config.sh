#!/bin/bash

source cloud-config.sh

PIVOT=10.0.1.101
CONFIG=config.properties

cluster=${!1}

test -z "$cluster" && {
	echo "unknown cluster name '$1'" >&2
	exit 1
}

cat >copy-script <<EOF
for ip in $(echo $cluster | sed 's/,/ /g')
do
	scp /tmp/$CONFIG root@\$ip:/srv/vismo
done
EOF

scp "$2" copy-script "root@$PIVOT:/tmp"
ssh "root@$PIVOT" 'sh -x /tmp/copy-script'
rm -f copy-script
