echo "inside nimbus:  start-supervisor.sh....................... "
echo "storm-home: $STORM_HOME"

 if [  -f "/etc/supervisor/conf.d/storm-nimbus.conf" ];
    then
        echo "storm-nimbus.conf found!"     # found

        while read line
            do
            echo "su-n........ $line"
           done < "/etc/supervisor/conf.d/storm-nimbus.conf"

     else
        echo "storm-nimbus.conf  not found!!! "
    fi

 if [  -f "/etc/supervisor/supervisord.conf" ];
    then
        echo "ssupervisord.conf found!"         # found
          while read line
            do
            echo "su........ $line"
           done < "/etc/supervisor/supervisord.conf"

     else
        echo "ssupervisord.conf  not found!!! "
    fi
 if [  -f "$STORM_HOME/bin/config-supervisord.sh" ];
    then
        echo "config-supervisord.sh found!"
     else
        echo "config-supervisord.sh  not found!!! "
    fi

 if [  -f "/usr/bin/config-supervisord.sh" ];  #found
    then
        echo "config-supervisord.sh found ...  usr/bin !"
     else
        echo "config-supervisord.sh... usr/bin  not found!!! "
    fi



#export IP=`hostname -i`
#export IP=`hostname`   #b7bb9202c74a

IPS=`hostname -i`
IFS=" "
read -r -a IP_ADDRESSES <<< "$IPS"
IP=${IP_ADDRESSES[0]}

echo -e  "\$1=$1"
export zookeeperConnectStr=$1
export ZOOKEEPER_PORT=$2

#export LOCALDIR=$3


#echo $zookeeperConnectStr | tr ',' ' '
ZKs="${zookeeperConnectStr//,/ }"

for i in $ZKs
    do
      if [[ i = 0 ]];
     then
         ZK_SERVERS="$'\n'"
      fi
       ZK_SERVERS+="- \"$i\" "$'\n'""
    done

echo "$ZK_SERVERS"



#sed -i -e "s/%zookeeper%/$ZK_SERVERS/g" $STORM_HOME/conf/storm.yaml
echo "storm.zookeeper.servers: $ZK_SERVERS" >> $STORM_HOME/conf/storm.yaml

sed -i -e "s/%port%/$ZOOKEEPER_PORT/g" $STORM_HOME/conf/storm.yaml


sed -i -e "s/%nimbus%/$IP/g" $STORM_HOME/conf/storm.yaml

PRIVATE_IP=`hostname`
#PRIVATE_IP=$(/sbin/ifconfig eth0 | grep 'inet addr:' | cut -d: -f
sed -i -e "s/%seeds%/$PRIVATE_IP/g" $STORM_HOME/conf/storm.yaml

#echo "storm.local.hostname: `hostname -i`" >> $STORM_HOME/conf/storm.yaml
#echo "storm.local.hostname: `hostname`" >> $STORM_HOME/conf/storm.yaml
echo "storm.local.hostname: $IP" >> $STORM_HOME/conf/storm.yaml

#sed -i -e "s/%localDir%/$LOCALDIR/g" $STORM_HOME/conf/storm.yaml

# create cutsom local storm directory . default is $STORM_HOME/storm-local
#dirName="storm-local-`hostname -i`"
#dirNameWithPath=$STORM_HOME"/"$dirName
#mkdir -p $dirNameWithPath
#echo "storm.local.dir: $dirNameWithPath" >> $STORM_HOME/conf/storm.yaml


        while read line
            do
            echo "storm.yaml........ $line"
           done < "$STORM_HOME/conf/storm.yaml"


#/usr/sbin/sshd
#nohup supervisord > /dev/null 2>&1 &

/usr/sbin/sshd

echo "inside nimbus:  start-supervisor.sh....................... going to call supervisord........ :) "
 supervisord -c /etc/supervisor/supervisord.conf

#supervisord -n -c /etc/supervisor/supervisord.conf