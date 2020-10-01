#!/bin/sh
  
set -x

cd src
javac  -cp ../interfaces.jar -d ../bin jobs/*.java

test $? -eq 0 || exit 1

javac -cp .:../interfaces.jar -d ../bin master/Master*.java


