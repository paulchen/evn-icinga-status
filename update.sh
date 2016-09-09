#!/bin/bash

cd `dirname $0`

target/appassembler/bin/evn-icinga-status evn.properties
