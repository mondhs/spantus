spantus
=======

[![Build Status](https://secure.travis-ci.org/mondhs/spantus.png)](https://travis-ci.org/mondhs/spantus)

signal(speech) analytical tool.

Nightly build results on twitter: [spantus_news@twitter](https://twitter.com/spantus_news) 

Nightly build standalone application can be found: [spnt-standalone](http://repository-spantus.forge.cloudbees.com/snapshot/org/spantus/spnt-standalone/ "maven repo deployments")

You will need maven to build spantus. More info: [Developer Notes](http://sourceforge.net/apps/mediawiki/spantus/index.php?title=DeveloperNotes "Spantus development")

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


Full build:

* Create bundle in spnt-work-ui/target/spnt-work-ui-${version}-standalone.zip
* word spotting software externals/integration/spnt-wordspot/
* speech recognition in spring cloud externals/integration/springcloud/*
* Build command:
mvn clean install -Dmaven.test.ignore=true -Dmaven.test.failure.ignore=true -Pintegeration-modules,copyRuntime
