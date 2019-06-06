#!/bin/bash

set -e

#set -v

#  a comma-separated list of all ZooKe
# eper servers in the ensemble:
export ZOO_SERVERS=$1

echo "Configuring ZK ensemble ------------ "

# if ZOO_SERVERS env is empty, exit
if [ -z "${ZOO_SERVERS}" ];then
     # die with unsuccessful shell script termination exit status # 3
      echo "ZOO_SERVERS is empty..aborting"
      exit 3
fi

# Put all ZooKeeper server IPs into an array:
IFS=', ' read -r -a ZOOKEEPER_SERVERS_ARRAY <<< "$ZOO_SERVERS"

export ZOOKEEPER_SERVERS_ARRAY=$ZOOKEEPER_SERVERS_ARRAY


# if ZOO_SERVERS env is empty, exit
if [ -z "${ZOO_SERVERS}" ];then
     # die with unsuccessful shell script termination exit status # 3
      echo "ZOO_SERVERS is empty..aborting"
      exit 3
fi


ZOOKEEPER_CONFIG2=
# now append information on every ZooKeeper node in the ensemble to the ZooKeeper config:
for index in "${!ZOOKEEPER_SERVERS_ARRAY[@]}"
do
    ZKID=$(($index+1))
    ZKIP=${ZOOKEEPER_SERVERS_ARRAY[index]}
   # if [ $ZKID == $ZOO_MY_ID ]
   # then
        # if IP's are used instead of hostnames, every ZooKeeper host has to specify itself as follows
    #    ZKIP=0.0.0.0
    #fi
    ZOOKEEPER_CONFIG2="$ZOOKEEPER_CONFIG2"$'\n'"server.$ZKID=$ZKIP:$ZOO_FOLLOWER_PORT:$ZOO_ELECTION_PORT"
done

echo "$ZOOKEEPER_CONFIG2" >> conf/zoo.cfg

echo "------ Final zk config:  "
while read line; do echo "$line"; done < conf/zoo.cfg

# start the server:
#/bin/bash bin/zkServer.sh start-foreground
 #/bin/bash -c "/usr/share/zookeeper/bin/zkServer.sh start-foreground"

  if [ $ZOO_MY_ID = 1 ]; then
     echo "---------- true myid 1 :> ------------ "
              /bin/bash /usr/share/zookeeper/bin/zkServer.sh start-foreground
              echo "---------- ZK must be running now! :) ------------ "
            else
               echo > ${ZOO_DATA_LOG_DIR}/backgroundLog.out
               nohup /usr/share/zookeeper/bin/zkServer.sh start > $ZOO_DATA_LOG_DIR/backgroundLog.out &
               echo "---------- ZK must be running now!! :} ------------ "
               tail -f $ZOO_DATA_LOG_DIR/backgroundLog.out
            fi

