#!/bin/bash
SPNT_HOME=./ #`pwd`;

if [ -d "$JAVA_HOME" -a -x "$JAVA_HOME/bin/java" ]; then
	JAVACMD="$JAVA_HOME/bin/java"
else
	JAVACMD=java
fi

TMP_CP=.
for a in $SPNT_HOME/lib/*; do
#echo "$a"
  TMP_CP="$TMP_CP":"$a"
done
echo "$TMP_CP"
$JAVACMD -classpath $TMP_CP org.spantus.work.ui.SpantusWorkMain

