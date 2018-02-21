#!/bin/bash

cd `dirname $0`

error=0
target/appassembler/bin/evn-icinga-status evn.properties > evn-update.log.tmp 2>&1 || error=1
cat evn-update.log.tmp >> evn-update.log

if [ "$error" -ne 0 ]; then
	cat evn-update.log.tmp
fi

rm -f evn-update.log.tmp

