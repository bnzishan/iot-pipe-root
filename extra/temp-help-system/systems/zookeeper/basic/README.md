https://github.com/gten/docker-zookeeper-cluster

# docker-zookeeper-cluster
A simple docker image to create a zookeeper cluster without much pain. 

## What is this used for?
There are several zookeeper docker images for creating standalone instances of zookeeper and running them. But there are no images that help in creating a zookeeper cluster across a single host or a multi-host environment. This image will help you to achieve the same.

# Docker image
```
docker pull jeygeethan/zookeeper-cluster
```
[Docker hub link](https://hub.docker.com/r/jeygeethan/zookeeper-cluster/)

# Looking for kafka cluster?
Go here : [kafka-cluster](https://github.com/gten/docker-kafka-cluster)

# Zookeeper Cluster (multi-host)
## Manual run of the cluster

Follow these steps:

1. Figure out if you will know the IP addresses/hosts of the hosts where you will be running the zookeepers containers.
  1. **UCP/Swarm** - You can figure this out if you are using a constraint node (-e constraint:node==host_name) in your docker-compose.yml file
  2. **Disconnected hosts** - You can figure this out by defining the set of hosts where you will deploy the container
2. Start running the containers in respective hosts or compile a sample docker-compose.yml as given below

### Generic script to run the zookeeper container:

The example given below is for a three node cluster. Change the parameters to suit your needs. $ID means the id/number of the node (either 1,2,3 etc) you are currently trying to run.

```
docker run -d --restart=always \
      -p 2181:2181 \
      -p 2888:2888 \
      -p 3888:3888 \
      -v /var/lib/zookeeper:/var/lib/zookeeper \
      -v /var/log/zookeeper:/var/log/zookeeper  \
      jeygeethan/zookeeper-cluster zookeeper_1_ip_or_hostname,zookeeper_2_ip_or_hostname,zookeeper_3_ip_or_hostname $ID
```

### Sample configuration for the three nodes:

Assume the three nodes are as follows:

1. docker_host_1
2. docker_host_2
3. docker_host_3

#### For first node (docker_host_1)

```
docker run -d --restart=always \
      -p 2181:2181 \
      -p 2888:2888 \
      -p 3888:3888 \
      -v /var/lib/zookeeper:/var/lib/zookeeper \
      -v /var/log/zookeeper:/var/log/zookeeper  \
      jeygeethan/zookeeper-cluster docker_host_1,docker_host_2,docker_host_3 1
```

#### For second node (docker_host_2)

```
docker run -d --restart=always \
      -p 2181:2181 \
      -p 2888:2888 \
      -p 3888:3888 \
      -v /var/lib/zookeeper:/var/lib/zookeeper \
      -v /var/log/zookeeper:/var/log/zookeeper  \
      jeygeethan/zookeeper-cluster docker_host_1,docker_host_2,docker_host_3 2
```

#### For third node (docker_host_3)

```
docker run -d --restart=always \
      -p 2181:2181 \
      -p 2888:2888 \
      -p 3888:3888 \
      -v /var/lib/zookeeper:/var/lib/zookeeper \
      -v /var/log/zookeeper:/var/log/zookeeper  \
      jeygeethan/zookeeper-cluster docker_host_1,docker_host_2,docker_host_3 3
```

## Sample docker-compose.yml for the cluster creation

```
version: '2'
services:
  zk_1:
    image: jeygeethan/zookeeper-cluster
    container_name: zk_1
    ports:
      - '2181:2181'
      - '2888:2888'
      - '3888:3888'
    volumes:
      - /var/lib/zookeeper:/var/lib/zookeeper
      - /var/log/zookeeper:/var/log/zookeeper
    command: docker_host_1,docker_host_2,docker_host_3 1
    environment:
      - constraint:node==docker_host_1
    networks:
      - some_overlay_network
  zk_2:
    image: jeygeethan/zookeeper-cluster
    container_name: zk_2
    ports:
      - '2181:2181'
      - '2888:2888'
      - '3888:3888'
    volumes:
      - /var/lib/zookeeper:/var/lib/zookeeper
      - /var/log/zookeeper:/var/log/zookeeper
    command: docker_host_1,docker_host_2,docker_host_3 2
    environment:
      - constraint:node==docker_host_2
    networks:
      - some_overlay_network
  zk_3:
    image: jeygeethan/zookeeper-cluster
    container_name: zk_3
    ports:
      - '2181:2181'
      - '2888:2888'
      - '3888:3888'
    volumes:
      - /var/lib/zookeeper:/var/lib/zookeeper
      - /var/log/zookeeper:/var/log/zookeeper
    command: docker_host_1,docker_host_2,docker_host_3 3
    environment:
      - constraint:node==docker_host_3
    networks:
      - some_overlay_network
networks:
  some_overlay_network:
    external: true
```
-------------------------------------

#!/usr/bin/env python

import os
import sys

zk_data_dir = '/var/lib/zookeeper'
zk_config_file = '/opt/zookeeper/conf/zoo.cfg'
zk_log_config_file = '/opt/zookeeper/conf/log4j.properties'

# From the environment, find details about the cluster.
num_servers = int(os.environ.get('ZK_CLUSTER_SIZE', 1))
myid = int(os.environ.get('ZK_SERVER_ID', 1))
servers = {}
for sid in range(1, num_servers + 1):
    servers[sid] = {
        'host': os.environ.get('ZK_SERVER_{}_HOST'.format(sid), '127.0.0.1'),
    }

print("DEBUG: Server List: {}".format(servers))

# build zookeeper node configuration
conf = {
    'tickTime': 2000,
    'initLimit': 10,
    'syncLimit': 5,
    'dataDir': zk_data_dir,
    'clientPort': 2181,
#    'quorumListenOnAllIPs': True,
    'autopurge.snapRetainCount':
        int(os.environ.get('ZK_MAX_SNAPSHOT_RETAIN_COUNT', 10)),
    'autopurge.purgeInterval':
        int(os.environ.get('ZK_PURGE_INTERVAL', 24)),
}

for node_id, props in servers.items():
    k = 'server.{}'.format(node_id)
    if node_id == myid:
        conf[k] = "0.0.0.0:2888:3888".format(**props)
    else:
        conf[k] = "{host}:2888:3888".format(**props)

print("DEBUG: Conf object: {}".format(conf))

# Write out the zookeeper configuration file.
with open(zk_config_file, 'w+') as f:
    for k, v in conf.items():
        f.write("{}={}\n".format(k, v))

LOG_PATTERN = (
    "%d{yyyy'-'MM'-'dd'T'HH:mm:ss.SSSXXX} %-5p [%-35.35t] [%-36.36c]: %m%n")

# Setup the logging configuration.
with open(zk_log_config_file, 'w+') as f:
    f.write("""# Log4j configuration, logs to rotating file
log4j.rootLogger=INFO,R
log4j.appender.R=org.apache.log4j.RollingFileAppender
log4j.appender.R.File=/var/log/zookeeper/zookeeper.log
log4j.appender.R.MaxFileSize=100MB
log4j.appender.R.MaxBackupIndex=10
log4j.appender.R.layout=org.apache.log4j.PatternLayout
log4j.appender.R.layout.ConversionPattern={}
""".format(LOG_PATTERN))

# Write out the 'myid' file in the data directory if in cluster mode.
if num_servers > 1:
    if not os.path.exists(zk_data_dir):
        os.makedirs(zk_data_dir, mode=0750)
    with open(os.path.join(zk_data_dir, 'myid'), 'w+') as f:
        f.write('{}\n'.format(myid))
    sys.stderr.write(
        'Starting node id#{} of a {}-node ZooKeeper cluster...\n'
        .format(myid, num_servers))
else:
    sys.stderr.write('Starting as a single-node ZooKeeper cluster...\n')

# Start ZooKeeper
os.execl('/opt/zookeeper/bin/zkServer.sh', 'zookeeper', 'start-foreground')


---------
