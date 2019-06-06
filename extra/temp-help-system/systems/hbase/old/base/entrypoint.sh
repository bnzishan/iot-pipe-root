#!/bin/bash

function addProperty() {
  local path=$1
  local name=$2
  local value=$3

  local entry="<property><name>$name</name><value>${value}</value></property>"
  local escapedEntry=$(echo $entry | sed 's/\//\\\//g')
  sed -i "/<\/configuration>/ s/.*/${escapedEntry}\n&/" $path
}

function configure() {
    local path=$1
    local module=$2
    local envPrefix=$3

    local var
    local value

    echo "Configuring $module"
    for c in `printenv | perl -sne 'print "$1 " if m/^${envPrefix}_(.+?)=.*/' -- -envPrefix=$envPrefix`; do
        name=`echo ${c} | perl -pe 's/___/-/g; s/__/_/g; s/_/./g'`
        var="${envPrefix}_${c}"
        value=${!var}
        echo " - Setting $name=$value"
        addProperty /etc/hbase/$module-site.xml $name "$value"
    done
}

configure /etc/hbase/hbase-site.xml hbase HBASE_CONF



function setRegionServers() {
  local region_servers=$1
  cat > /etc/hbase/regionservers
  # for regionServer in $REGION_SERVERS; do
  for regionServer in $(echo $region_servers | sed "s/,/ /g"); do
    echo $regionServer >> /etc/hbase/regionservers
  done
}
function configureRegionServers(){
    while IFS='=' read -r name value ; do
      if [[ $name == *'REGIONSERVERS'* ]]; then
        echo "$name" ${!name}
        setRegionServers ${!name}
      fi
    done < <(env)
}

configureRegionServers

function setBackupMasters() {
  cat > /etc/hbase/backup-masters
  for backupMaster in $BACKUP_MASTERS; do
    echo $backupMaster >> /etc/hbase/backup-masters
  done
}

setBackupMasters

function wait_for_it()
{
    local serviceport=$1
    local service=${serviceport%%:*}
    local port=${serviceport#*:}
    local retry_seconds=5
    local max_try=100
    let i=1

    nc -z $service $port
    result=$?

    until [ $result -eq 0 ]; do
      echo "[$i/$max_try] check for ${service}:${port}..."
      echo "[$i/$max_try] ${service}:${port} is not available yet"
      if (( $i == $max_try )); then
        echo "[$i/$max_try] ${service}:${port} is still not available; giving up after ${max_try} tries. :/"
        exit 1
      fi

      echo "[$i/$max_try] try in ${retry_seconds}s once again ..."
      let "i++"
      sleep $retry_seconds

      nc -z $service $port
      result=$?
    done
    echo "[$i/$max_try] $service:${port} is available."
}

for i in "${SERVICE_PRECONDITION[@]}"
do
    wait_for_it ${i}
done



exec $@
