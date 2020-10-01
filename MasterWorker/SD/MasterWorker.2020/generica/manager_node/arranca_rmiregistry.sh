#!/bin/sh

set -x

CLASSPATH=.:interfaces.jar  rmiregistry $*
