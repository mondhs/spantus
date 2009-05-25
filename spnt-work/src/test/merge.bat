@echo off
cd ..\..
set NOISES=C:/home/studijos/wav/noises/11k/
rem set NOISES=C:\home\studijos\wav\noises\11k\
set SIGNAL=C:/home/studijos/wav/merge/521.wav

for %%i in ("%NOISES%\*.wav") do (
mvn exec:java -Dexec.mainClass="org.spantus.work.util.MergeNoise" -Dexec.args="%SIGNAL% %%i" -o )
java
GOTO :eof







:eof


