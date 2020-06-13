#!/bin/sh

# change work directory
cd "$(dirname "$0")"

# default JVM options
jvm_options=`java -cp antic.jar org.antic.JvmOptions --cli`

# start kernel
java ${jvm_options} -cp antic.jar org.antic.Main --cli "$@"
