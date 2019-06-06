FROM alpine:3.9
MAINTAINER BNZ

ENV LANG C.UTF-8


RUN apk add --update --no-cache bash && rm -rf /var/cache/apk/*

# add a simple script that can auto-detect the appropriate JAVA_HOME value
# based on whether the JDK or only the JRE is installed
RUN { \
		echo '#!/bin/sh'; \
		echo 'set -e'; \
		echo; \
		echo 'dirname "$(dirname "$(readlink -f "$(which javac || which java)")")"'; \
	} > /usr/local/bin/docker-java-home \
	&& chmod +x /usr/local/bin/docker-java-home

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
ENV PATH $PATH:/usr/lib/jvm/java-1.8-openjdk/jre/bin:/usr/lib/jvm/java-1.8-openjdk/bin
ENV JAVA_VERSION 8u191
#ENV JAVA_ALPINE_VERSION 8.191.12-r0
ENV JAVA_ALPINE_VERSION  8.201.08-r0

RUN set -x \
	&& apk add --no-cache \
		openjdk8="$JAVA_ALPINE_VERSION" \
	&& [ "$JAVA_HOME" = "$(docker-java-home)" ]

# The used ZooKeeper version can also be supplied like this with the build command like this:
# --build-arg BIN_VERSION=zookeeper-3.4.8
ARG BIN_VERSION=zookeeper-3.4.13
#ARG BIN_VERSION=zookeeper-3.4.13


# pre-install setup
Run mkdir -p /usr/share/zookeeper
WORKDIR /usr/share/zookeeper

# Install wget and tar and zookeeper
RUN apk add  --no-cache wget tar \
# && wget -q -N http://mirror.dkd.de/apache/zookeeper/stable/$BIN_VERSION.tar.gz \
&& wget -q -N https://apache.org/dist/zookeeper/stable/$BIN_VERSION.tar.gz \
&& tar --strip-components=1 -C /usr/share/zookeeper -xvf ${BIN_VERSION}.tar.gz \
&& rm $BIN_VERSION.tar.gz \
&& rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# conf Dir
ENV confDir=/usr/share/zookeeper/conf

RUN ln -s /usr/share/zookeeper /app
RUN ln -s /usr/share/zookeeper/conf /config

Run mkdir -p /var/lib/zookeeper \
&&  mkdir -p /var/log/zookeeper

# set path to zookeeper home dir, CHECK it
ENV ZOOKEEPER_HOME="/usr/share/zookeeper"
ENV PATH $PATH:$ZOOKEEPER_HOME/bin \
    ZOOCFGDIR=$confDir

# cd into home dir and copy script in it
WORKDIR "${ZOOKEEPER_HOME}"

# add startup script
ADD entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh


VOLUME ["/var/lib/zookeeper", "/var/log/zookeeper", "/usr/share/zookeeper/conf"]
#VOLUME ["$ZOO_DATA_DIR", "$ZOO_DATA_LOG_DIR", "$confDir"]


# default parameters for config file:

ARG ZOO_TICK_TIME=2000
ENV ZOO_TICK_TIME=${ZOO_TICK_TIME}

ARG ZOO_DATA_DIR=/var/lib/zookeeper
ENV ZOO_DATA_DIR=${ZOO_DATA_DIR}

ARG ZOO_DATA_LOG_DIR=/var/log/zookeeper
ENV ZOO_DATA_LOG_DIR=${ZOO_DATA_LOG_DIR}

ARG ZOO_INIT_LIMIT=10
ENV ZOO_INIT_LIMIT=${ZOO_INIT_LIMIT}

ARG ZOO_SYNC_LIMIT=2
ENV ZOO_SYNC_LIMIT=${ZOO_SYNC_LIMIT}

ARG ZOO_MAX_CLIENT_CNXNS=100
ENV ZOO_MAX_CLIENT_CNXNS=${ZOO_MAX_CLIENT_CNXNS}

ARG ZOO_QUORUM_LISTEN_ON_ALL_IPS=true
ENV ZOO_QUORUM_LISTEN_ON_ALL_IPS=${ZOO_QUORUM_LISTEN_ON_ALL_IPS}

# others

ARG ZOO_SERVERS=
ENV ZOO_SERVERS=${ZOO_SERVERS}

ARG ZOO_MY_ID=
ENV ZOO_MY_ID=${ZOO_MY_ID}

ARG ZOO_CLIENT_PORT=2181
ENV ZOO_CLIENT_PORT=${ZOO_CLIENT_PORT}

ARG ZOO_FOLLOWER_PORT=2888
ENV ZOO_FOLLOWER_PORT=${ZOO_FOLLOWER_PORT}

ARG ZOO_ELECTION_PORT=3888
ENV ZOO_ELECTION_PORT=${ZOO_ELECTION_PORT}

ARG CLIENT_PORTS=
ENV CLIENT_PORTS=${CLIENT_PORTS}

ARG FOLLOWER_PORTS=
ENV FOLLOWER_PORTS=${FOLLOWER_PORTS}

ARG ELECTION_PORTS=
ENV ELECTION_PORTS=${ELECTION_PORTS}

EXPOSE $ZOO_PORT $ZOO_FOLLOWER_PORT $ZOO_ELECTION_PORT


ENTRYPOINT ["/usr/share/zookeeper/entrypoint.sh"]



