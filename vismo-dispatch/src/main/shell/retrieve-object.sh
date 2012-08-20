#!/bin/bash

curl -v \
	-X GET \
	--user 'vassilis@ntua:changeme' \
	-H 'Accept: application/cdmi-object' \
	-H 'Content-Type: application/cdmi-object' \
	-H 'X-CDMI-Specification-Version: 1.0' \
	http://10.0.1.101/vision-cloud/object-service/ntua/$1/$2
