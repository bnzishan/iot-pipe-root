FROM java:openjdk-8-jre
#FROM openjdk:8-alpine

MAINTAINER BNZ

# The used ZooKeeper version can also be supplied like this with the build command like this:
# --build-arg BIN_VERSION=zookeeper-3.4.8
ARG BIN_VERSION=zookeeper-3.5.2-alpha


Run mkdir -p /usr/share/zookeeper
WORKDIR /usr/share/zookeeper

EXPOSE 2181 2888 3888

# Install and set everything up
RUN apt-get install -y wget tar
RUN \
   wget -q -N http://mirror.dkd.de/apache/zookeeper/$BIN_VERSION/$BIN_VERSION.tar.gz \
&& tar --strip-components=1 -C /usr/share/zookeeper -xvf ${BIN_VERSION}.tar.gz \
&& rm $BIN_VERSION.tar.gz \
&& rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN ln -s /usr/share/zookeeper /app
RUN ln -s /usr/share/zookeeper/conf /config

// added, check it
ENV ZK_HOME="/usr/share/zookeeper"

# default parameters for config file:
ENV tickTime=2000
ENV dataDir=/var/lib/zookeeper/
ENV dataLogDir=/var/log/zookeeper/
ENV clientPort=2181
ENV initLimit=5
ENV syncLimit=2

# add startup script
ADD entrypoint.sh entrypoint.sh
RUN chmod +x entrypoint.sh

ENTRYFROM java:openjdk-8-jre
     #FROM openjdk:8-alpine

     MAINTAINER BNZ

     # The used ZooKeeper version can also be supplied like this with the build command like this:
     # --build-arg BIN_VERSION=zookeeper-3.4.8
     ARG BIN_VERSION=zookeeper-3.5.2-alpha


     Run mkdir -p /usr/share/zookeeper
     WORKDIR /usr/share/zookeeper

     EXPOSE 2181 2888 3888

     # Install and set everything up
     RUN apt-get install -y wget tar
     RUN \
        wget -q -N http://mirror.dkd.de/apache/zookeeper/$BIN_VERSION/$BIN_VERSION.tar.gz \
     && tar --strip-components=1 -C /usr/share/zookeeper -xvf ${BIN_VERSION}.tar.gz \
     && rm $BIN_VERSION.tar.gz \
     && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

     RUN ln -s /usr/share/zookeeper /app
     RUN ln -s /usr/share/zookeeper/conf /config

     // added, check it
     ENV ZK_HOME="/usr/share/zookeeper"

     # default parameters for config file:
     ENV tickTime=2000
     ENV dataDir=/var/lib/zookeeper/
     ENV dataLogDir=/var/log/zookeeper/
     ENV clientPort=2181
     ENV initLimit=5
     ENV syncLimit=2

     # add startup script
     ADD entrypoint.sh entrypoint.sh
     RUN chmod +x entrypoint.sh

     ENTRYPOINT ["/usr/share/zookeeper/entrypoint.sh"]POINT ["/usr/share/zookeeper/entrypoint.sh"]