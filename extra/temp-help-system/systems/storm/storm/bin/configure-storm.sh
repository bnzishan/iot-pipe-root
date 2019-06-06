#!/usr/bin/env bash

######  changes reflect use of zookeeper cluster rather than one zk node ######

HOST=`hostname -i`


#ZOOKEEPER_ADDRESS=${ZOOKEEPER_ADDRESS:=$ZK_PORT_2181_TCP_ADDR}
ZOOKEEPER_PORT=${ZOOKEEPER_PORT:=2181}
NIMBUS_ADDRESS=${NIMBUS_ADDRESS:=$NIMBUS_PORT_6627_TCP_ADDR}
                    echo "$NIMBUS_ADDRESS"
DRPC_PORT=${DRPC_PORT:=3772}
DRPCI_PORT=${DRPCI_PORT:=3773}
NIMBUS_THRIFT_PORT=${NIMBUS_THRIFT_PORT:=6627}

LOCAL_HOSTNAME=${LOCAL_HOSTNAME:=$HOST}

if [ -z "$ZOOKEEPER_SERVERS" ]; then
        echo "ZOOKEEPER_SERVERS not set"
        exit 1
fi

if [ -z "$NIMBUS_ADDRESS" ]; then
        echo "NIMBUS_ADDRESS not set"
        exit 1
fi

### zookeeper configuration

sed -i -e "s|%zookeeper.port%|$ZOOKEEPER_PORT|g" $STORM_HOME/conf/storm.yaml

#  single ZK
#sed -i -e "s|%zookeeper%|$ZOOKEEPER_ADDRESS|g" $STORM_HOME/conf/storm.yaml

#  ZK cluster
IFS=, read -ra ADDR <<< "$ZOOKEEPER_SERVERS"

ZK_LINES=
for zk_server in "${ADDR[@]}" ; do
    ZK_LINES="$ZK_LINES - \"$zk_server\"\\n"
done

sed -i -e "s/%ZOOKEEPER_SERVERS%/$ZK_LINES/g" $STORM_HOME/conf/storm.yaml
# #  ZK cluster ends

### nimbus configuration
sed -i -e "s|%nimbus%|$NIMBUS_ADDRESS|g" $STORM_HOME/conf/storm.yaml
sed -i -e "s|%nimbus.thrift.port%|$NIMBUS_THRIFT_PORT|g" $STORM_HOME/conf/storm.yaml

### drpc configuration
sed -i -e "s|%drpc.port%|$DRPC_PORT|g" $STORM_HOME/conf/storm.yaml
sed -i -e "s|%drpci.port%|$DRPCI_PORT|g" $STORM_HOME/conf/storm.yaml

### misc configuration
sed -i -e "s|%local.hostname%|$LOCAL_HOSTNAME|g" $STORM_HOME/conf/storm.yaml
