spantus
=======

signal(speech) analytical tool.
You will need maven to build spantus. More info: https://sourceforge.net/apps/mediawiki/spantus/index.php?title=DeveloperNotes

[Developer Notes](http://sourceforge.net/apps/mediawiki/spantus/index.php?title=DeveloperNotes "Spantus development")

JDK 7:

* Build
sh build.sh

* Run the tool:
sh run-tool.sh


JDK 6:

* Build
mvn clean install  -Djdk6

* Run the tool:
cd spnt-work-ui
mvn exec:java -Dexec.mainClass="org.spantus.work.ui.SpantusWorkMain" -Djdk6
