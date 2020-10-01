#!/bin/sh
  
set -x

cd src
javac -cp ../interfaces.jar -d ../bin worker/WorkerImpl*.java worker/WorkerSrv.java

