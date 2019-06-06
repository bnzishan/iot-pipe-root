#FROM java:openjdk-8-jre
#FROM openjdk:8-alpine

FROM bnz/base-alpine:v0.0
MAINTAINER BNZ

# The used ZooKeeper version can also be supplied like this with the build command like this:
# --build-arg BIN_VERSION=zookeeper-3.4.8
#ARG BIN_VERSION=zookeeper-3.5.2-alpha
ARG BIN_VERSION=zookeeper-3.4.13

# default parameters for config file:

#  for VAT:
#ARG ZOO_MY_ID=0
#ENV ZOO_MY_ID=${ZOO_MY_ID}


ARG ZOO_TICK_TIME=2000
ENV ZOO_TICK_TIME=${ZOO_TICK_TIME}

ARG ZOO_DATA_DIR=/var/lib/zookeeper
ENV ZOO_DATA_DIR=${ZOO_DATA_DIR}

ARG ZOO_DATA_LOG_DIR=/var/log/zookeeper
ENV ZOO_DATA_LOG_DIR=${ZOO_DATA_LOG_DIR}

ARG ZOO_PORT=2181
ENV ZOO_PORT=${ZOO_PORT}

ENV ZOO_INIT_LIMIT=5
ENV ZOO_INIT_LIMIT=${ZOO_INIT_LIMIT}

ENV ZOO_SYNC_LIMIT=2
ENV ZOO_SYNC_LIMIT=${ZOO_SYNC_LIMIT}

# others
ENV confDir=/usr/share/zookeeper/conf

ARG ZOO_FOLLOWER_PORT=2888
ENV ZOO_FOLLOWER_PORT=${ZOO_FOLLOWER_PORT}

ARG ZOO_ELECTION_PORT=3888
ENV ZOO_ELECTION_PORT=${ZOO_ELECTION_PORT}




# pre-install setup
Run mkdir -p /usr/share/zookeeper
WORKDIR /usr/share/zookeeper

# Install wget and tar and zookeeper
RUN apk add  --no-cache wget tar \
&& wget -q -N http://mirror.dkd.de/apache/zookeeper/stable/$BIN_VERSION.tar.gz \
&& tar --strip-components=1 -C /usr/share/zookeeper -xvf ${BIN_VERSION}.tar.gz \
&& rm $BIN_VERSION.tar.gz \
&& rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN ln -s /usr/share/zookeeper /app
RUN ln -s /usr/share/zookeeper/conf /config

# set path to zookeeper home dir, CHECK it
ENV ZOOKEEPER_HOME="/usr/share/zookeeper"
ENV PATH $PATH:$ZOOKEEPER_HOME/bin \
    ZOOCFGDIR=$confDir

# cd into home dir and copy script in it
WORKDIR "${ZOOKEEPER_HOME}"
# add startup script
ADD entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh


#VOLUME [" /var/lib/zookeeper", "/var/log/zookeeper", "/usr/share/zookeeper/conf"]
VOLUME ["$ZOO_DATA_DIR", "$ZOO_DATA_LOG_DIR", "$confDir"]

EXPOSE $ZOO_PORT $ZOO_FOLLOWER_PORT $ZOO_ELECTION_PORT


ENTRYPOINT ["/usr/share/zookeeper/entrypoint.sh"]
#for keeping the container alive
CMD ["tail", "-f", "/dev/null"]

