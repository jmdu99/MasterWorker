#!/bin/sh

set -x

cd bin
java -Djava.security.policy=../permisos -Djava.rmi.server.codebase=file:../../master_node/bin/ -cp .:../interfaces.jar worker.WorkerSrv $*

