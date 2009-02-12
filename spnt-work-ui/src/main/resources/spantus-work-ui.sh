#!/bin/bash
SPNT_HOME=../ #`pwd`;
TMP_CP=.
for a in $SPNT_HOME/lib/*; do
#echo "$a"
  TMP_CP="$TMP_CP":"$a"
done
echo "$TMP_CP"
java -classpath $TMP_CP:spnt-work-ui-0.0.1.jar org.spantus.work.ui.SpantusWorkMain

#java -classpath .:./lib/spnt-core-0.0.1.jar:./lib/spnt-math-0.0.1.jar:./lib/spnt-work-0.0.1.jar:./lib/spnt-chart-0.0.1.jar:./lib/spnt-extract-0.0.1.jar:./lib/spnt-mpeg7-0.0.1.jar -jar spnt-work-ui-0.0.1.jar
