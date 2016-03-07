#!/bin/bash

REPO=$1


echo ${REPO}_output.txt
python bug_metrics.py $REPO > bugs.txt
sed -i.bak '/Already up-to-date./d' bugs.txt
./extract $REPO $REPO
cp ./${REPO}_output.txt ./formatted
spark-submit --class "SimpleMetrics" --master local[1] target/scala-2.10/simple-project_2.10-1.0.jar 
mv all/part-00000 ./${REPO}_all
mv bugs.txt bugs_${REPO}.txt
mv formatted formatted_${REPO}
rm -rf all

