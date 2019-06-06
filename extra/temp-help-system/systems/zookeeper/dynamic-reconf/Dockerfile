#FROM ubuntu:trusty
#FROM ubuntu:16.04
#FROM java:8
FROM openjdk:8-alpine

MAINTAINER BNZ

#RUN apt-get update && apt-get install -y vim

RUN apk add --update bash && rm -rf /var/cache/apk/*

RUN mkdir /usr/local/apache-zookeeper

ADD zookeeper-3.5.2-alpha.tar.gz /usr/local/apache-zookeeper/

RUN rm -f /tmp/zookeeper-3.5.2-alpha.tar.gz

RUN ln -s /usr/local/apache-zookeeper/zookeeper-3.5.2-alpha /usr/local/apache-zookeeper/current

RUN ln -s /usr/local/apache-zookeeper/zookeeper-3.5.2-alpha /app
RUN ln -s /usr/local/apache-zookeeper/zookeeper-3.5.2-alpha/conf /config

COPY zk-config /app/bin
COPY startup /

COPY zoo.cfg /config/
COPY zoo_replicated1.cfg.dynamic /config/

#COPY wait-for-step.sh /
#COPY execute-step.sh /
#COPY finish-step.sh /

#ENV ZOOKEEPER_HOME="/usr/local/apache-zookeeper/zookeeper-3.5.2-alpha"
#ENV PATH $PATH:$ZOOKEEPER_HOME/bin

CMD ["/startup"]