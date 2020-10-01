#!/bin/sh
  
set -x

cd src
javac -cp ../interfaces.jar -d ../bin manager/ManagerImpl*.java manager/ManagerSrv.java

