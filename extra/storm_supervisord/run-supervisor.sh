#!/bin/sh



cd /usr/bin

bash start-supervisor.sh $ZOOKEEPER_CONNECT $ZOOKEEPER_PORT

cd /sensemark

echo $(date +%H:%M:%S.%N | cut -b1-12)" : Running Storm Supervisor ...................................."

java -cp sensemark2benchmark.jar org.hobbit.core.run.ComponentStarter org.hobbit.sdk.sensemark2.system.storm.StormSupervisor





