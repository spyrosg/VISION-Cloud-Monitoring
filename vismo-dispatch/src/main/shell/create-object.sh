#!/bin/bash

curl -v \
	-X PUT \
	--user 'vassilis@ntua:changeme' \
	-H 'Accept: application/cdmi-object' \
	-H 'Content-Type: application/cdmi-object' \
	-H 'X-CDMI-Specification-Version: 1.0' \
	-d '{ "metadata": { "owner": "vassilis" }, "value": "this is my foo object" }' \
	http://10.0.1.101/vision-cloud/object-service/ntua/$1/$2
