#!/usr/bin/env bash

export NIMBUS_ADDRESS=`hostname -i`

#export IP=`hostname -i`
#sed -i -e "s/%nimbus%/$IP/g" $STORM_HOME/conf/storm.yaml

/usr/bin/configure-storm.sh
supervisord
