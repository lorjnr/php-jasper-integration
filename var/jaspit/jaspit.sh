#! /bin/bash

DIR=`dirname $0`

JPC=
JPC=${JPC}$DIR/jasp.jar:
JPC=${JPC}$DIR/lib/jdom.jar:
JPC=${JPC}$DIR/lib/jasperreports-3.0.1.jar:
JPC=${JPC}$DIR/lib/commons-digester-1.7.jar:
JPC=${JPC}$DIR/lib/commons-collections-2.1.jar:
JPC=${JPC}$DIR/lib/commons-logging-1.0.2.jar:
JPC=${JPC}$DIR/lib/commons-beanutils-1.7.jar


java -classpath $JPC ch.bayo.jasper.cockpit.Main

