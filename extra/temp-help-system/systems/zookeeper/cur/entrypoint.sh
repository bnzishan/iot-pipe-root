#!/bin/bash

set -e

echo " -------------- zk entrypoint   --------- --------- "
echo Your container args are: "$@"

for i in "$@"
 do
    echo "Arg #$cnt= $arg"
    let "cnt+=1"
 done

echo -e  "\$1=$1"
echo -e "\$2=$2"

echo  con_name=$HOBBIT_CONTAINER_NAME
echo myid_file=$

# create data and blog directories:
#mkdir -p $ZOO_DATA_DIR
#mkdir -p $ZOO_DATA_LOG_DIR

#  vat of this ZooKeeper node:
export ZOO_MY_ID=$ZOO_MY_ID
# create myID file:
echo "$ZOO_MY_ID" | tee $ZOO_DATA_DIR/myid

echo "------ dockerutils id ------  "
while read line; do echo "$line"; done < $ZOO_DATA_DIR/myid

export CLIENT_PORTS=$CLIENT_PORTS
IFS=', ' read -r -a ZOOKEEPER_CLIENT_PORT_ARRAY <<< "$CLIENT_PORTS"
INDEX=$(($ZOO_MY_ID-1))
export ZOO_PORT=${ZOOKEEPER_CLIENT_PORT_ARRAY[index]}

# now build the ZooKeeper configuration file:
ZOOKEEPER_CONFIG=
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"tickTime=$ZOO_TICK_TIME"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"dataDir=$ZOO_DATA_DIR"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"dataLogDir=$ZOO_DATA_LOG_DIR"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"clientPort=$ZOO_PORT"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"initLimit=$ZOO_INIT_LIMIT"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"syncLimit=$ZOO_SYNC_LIMIT"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"maxClientCnxns=$ZOO_MAX_CLIENT_CNXNS"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"quorumListenOnAllIPs=$ZOO_QUORUM_LISTEN_ON_ALL_IPS"
ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"cnxTimeout=60000"


#  a comma-separated list of all ZooKeeper servers in the ensemble:
export ZOO_SERVERS=$ZOO_SERVERS
# Put all ZooKeeper server IPs into an array:
IFS=', ' read -r -a ZOOKEEPER_SERVERS_ARRAY <<< "$ZOO_SERVERS"


export FOLLOWER_PORTS=$FOLLOWER_PORTS
export ELECTION_PORTS=$ELECTION_PORTS
# Put all ZooKeeper client,election,follower Ports into respective arrays:
IFS=', ' read -r -a ZOOKEEPER_FOLLOWER_PORTS_ARRAY <<< "$FOLLOWER_PORTS"
IFS=', ' read -r -a ZOOKEEPER_ELECTION_PORTS_ARRAY <<< "$ELECTION_PORTS"

export ZOOKEEPER_SERVERS_ARRAY=$ZOOKEEPER_SERVERS_ARRAY

# now append information on every ZooKeeper node in the ensemble to the ZooKeeper config:
for index in "${!ZOOKEEPER_SERVERS_ARRAY[@]}"

do
    ZKID=$(($index+1))
    ZKIP=${ZOOKEEPER_SERVERS_ARRAY[index]}
    ZKFP=${ZOOKEEPER_FOLLOWER_PORTS_ARRAY[index]}
    ZKEP=${ZOOKEEPER_ELECTION_PORTS_ARRAY[index]}
    if [ $ZKID == $ZOO_MY_ID ]
    then
         #if IP's are used instead of hostnames, every ZooKeeper host has to specify itself as follows
        ZKIP=0.0.0.0
    fi
    ZOOKEEPER_CONFIG="$ZOOKEEPER_CONFIG"$'\n'"server.$ZKID=$ZKIP:$ZKFP:$ZKEP"
done

# Finally, write config file:
echo "$ZOOKEEPER_CONFIG" | tee conf/zoo.cfg

echo "------ Final zk configuration ------  "

while read line; do echo "$line"; done < conf/zoo.cfg

# start the server:
#/bin/bash bin/zkServer.sh start-foreground

    echo > ${ZOO_DATA_LOG_DIR}/backgroundLog.out
    nohup  /usr/share/zookeeper/bin/zkServer.sh start > $ZOO_DATA_LOG_DIR/backgroundLog.out
    echo "zk must be started by now... "
    tail -f -n 100 $ZOO_DATA_LOG_DIR/backgroundLog.out

