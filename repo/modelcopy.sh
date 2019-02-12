#!/usr/bin/env bash

dslProjectDir=/jcodes/dev/projects/bw-payment-dsl/
commonJar=payment-common-1.0-SNAPSHOT.jar
coreJar=payment-core-1.0-SNAPSHOT.jar

cp $dslProjectDir'common/target/'$commonJar ./repo/
if [ $? == 0 ]; then
    echo "===> " $commonJar" was copied successfully"
else
echo "===> " "An unknown error occurred copying "$commonJar
fi

cp $dslProjectDir'core/target/'$coreJar ./repo/
if [ $? == 0 ]
then
    echo "===> " $coreJar" was copied successfully"
else
echo "===> " "An unknown error occurred copying "$coreJar
fi