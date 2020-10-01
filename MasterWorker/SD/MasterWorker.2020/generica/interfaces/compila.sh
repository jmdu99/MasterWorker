#!/bin/sh
  
set -x

cd src
javac -d ../bin interfaces/Manager.java  interfaces/TaskCB.java  interfaces/Task.java  interfaces/Worker.java

test $? -eq 0 || exit 1

cd ../bin

jar cf ../interfaces.jar interfaces/Manager.class interfaces/TaskCB.class interfaces/Task.class interfaces/Worker.class

