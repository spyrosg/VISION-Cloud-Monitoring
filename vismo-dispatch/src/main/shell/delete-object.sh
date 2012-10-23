#!/bin/bash

curl -v \
	-X DELETE \
	--user 'vassilis@ntua:123' \
	-H 'Accept: application/cdmi-object' \
	-H 'Content-Type: application/cdmi-object' \
	-H 'X-CDMI-Specification-Version: 1.0' \
	http://10.0.2.214/vision-cloud/object-service/ntua/$1/$2
