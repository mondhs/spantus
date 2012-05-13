#!/bin/bash
SPNT_HOME=./ #`pwd`;
TMP_CP=.
for a in $SPNT_HOME/lib/*; do
#echo "$a"
  TMP_CP="$TMP_CP":"$a"
done
echo "$TMP_CP"
java -classpath $TMP_CP org.spantus.work.ui.SpantusWorkMain

