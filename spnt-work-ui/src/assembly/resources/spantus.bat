@echo off
set JARS=%~dp0\lib
set TMP_CP=.
for %%i in ("%JARS%\*.jar") do (call :append_classpath "%%i")

java -classpath %TMP_CP% org.spantus.work.ui.SpantusWorkMain






:append_classpath
set TMP_CP=%TMP_CP%;%1
GOTO :eof

:eof