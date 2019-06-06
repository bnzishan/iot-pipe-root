#!/bin/bash

set -e

# the first argument provided is vat of this ZooKeeper node:
export ZOO_MY_ID=$1

# create data and blog directories:
mkdir -p $ZOO_DATA_DIR
mkdir -p $ZOO_DATA_LOG_DIR

# create myID file:
echo "$ZOO_MY_ID" | tee $ZOO_DATA_DIR/myid

# now build the ZooKeeper configuration file:
ZOOKEEPER_CONFIG=
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"tickTime=$ZOO_TICK_TIME"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"dataDir=$ZOO_DATA_DIR"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"dataLogDir=$ZOO_DATA_LOG_DIR"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"clientPort=$ZOO_PORT"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"initLimit=$ZOO_INIT_LIMIT"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"syncLimit=$ZOO_SYNC_LIMIT"

# Finally, write config file:
echo "$ZOOKEEPER_CONFIG" | tee conf/zoo.cfg

while read line; do echo "$line"; done < conf/zoo.cfg


# This will exec the CMD from your Dockerfile, i.e. "CMD tail -f /dev/null"
exec "$@"