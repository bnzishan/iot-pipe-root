FROM oraclelinux:7-slim

ENTRYPOINT ["/bin/bash", "-l", "-i"]

ADD ./jdk-8u121-linux-x64.tar.gz /opt/
ADD ./zookeeper-3.4.10.tar.gz /opt/

RUN mv /opt/jdk* /opt/jdk && mv /opt/zookeeper* /opt/zookeeper

RUN echo "PATH=/opt/jdk/bin:/opt/zookeeper/bin:\$PATH" > /etc/profile.d/zkpath.sh
