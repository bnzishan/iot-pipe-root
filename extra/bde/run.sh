#!/bin/sh



cd /app/bin/

bash -c kafka-init

create-topics.sh &
#unset KAFKA_CREATE_TOPICS

#echo "kafka done"

until $KAFKA_HOME/bin/kafka-topics.sh --list --zookeeper $KAFKA_ZOOKEEPER_CONNECT  | fgrep -q Sensors ;do
    >&2 echo "XXX $0 topic is unavailable - waiting"
    sleep 1
done

echo XXX $0 OK topic Sensors is available

cd /benchmark

echo $(date +%H:%M:%S.%N | cut -b1-12)" : Running Kafka Manager ...................................."

java -cp main-module-1.0.0.jar org.hobbit.core.run.ComponentStarter org.hobbit.sdk.iotpipeline_bm.system.kafka.KafkaManager





