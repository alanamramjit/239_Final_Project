#!/bin/bash

REPO=$1

echo ${REPO}_output.txt
python bug_metrics.py $REPO > spark/bugs
sed -i.bak '/Already up-to-date./d' spark/bugs
./extract $REPO $REPO
# cp ./${REPO}_output.txt ./formatted
cp ./${REPO}_method_ids.txt spark/method_ids
cp ./${REPO}_method_map.txt spark/method_map
cd spark
sbt run #spark-submit --class "SimpleMetrics" target/scala-2.10/simple-project_2.10-1.0.jar 
mv all/part-00000 ../${REPO}_all
mv bugs bugs_${REPO}.txt
rm -rf all