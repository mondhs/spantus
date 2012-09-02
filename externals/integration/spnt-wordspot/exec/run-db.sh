#!/bin/bash
mvn exec:java -Dexec.mainClass="org.hsqldb.Server" -Dexec.args="-database.0 file:target/spnt-exp -dbname.0 spnt-exp"

