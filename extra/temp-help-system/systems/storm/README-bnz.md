
## 

OS=  Ubuntu:trusty 
Java= openjdk-7-jre-headless

## Base image  wurstmeister/base

FROM ubuntu:trusty
MAINTAINER Wurstmeister 

#installs openjdk and openssh server
RUN apt-get update; apt-get install -y unzip openjdk-7-jre-headless wget supervisor docker.io openssh-server

# set JAVE_HOME env var
ENV JAVA_HOME /usr/lib/jvm/java-7-openjdk-amd64/

# Setup the password for root
RUN echo 'root:wurstmeister' | chpasswd

# Make sure the sshd folder exists
RUN mkdir /var/run/sshd

# Reconfigure the SSH to accept login as root, with the specified password
RUN sed -i 's/PermitRootLogin without-password/PermitRootLogin yes/' /etc/ssh/sshd_config

# Expose port 22 (SSH)
EXPOSE 22


--------

changes can be done in the base image above:

# Environment variable, used to setup root password (instead of having a fixed one)
ENV SSH_ROOT_PASS password_pls_change

# Setup the password for root
RUN echo "root:$SSH_ROOT_PASS" | chpasswd
---------

## Base image sunside/java-base

FROM java:openjdk-8-jdk
MAINTAINER Markus Mayer <widemeadows@gmail.com>
 
RUN apt-get update; apt-get install -y unzip wget supervisor


-----------------------------
STORM:YAML 

########### These MUST be filled in for a storm configuration
 storm.zookeeper.servers:
     - "10.0.0.79"
     - "10.0.0.124"
     - "10.0.0.84"
#     - "localhost"
#

---------------------------
(not used yet )

test to check if  zk is available:

first_zk=$(echo "$STORM_ZOOKEEPER_SERVERS" | cut -d, -f1)

# wait for zookeeper to become available
if [ "$ZOOKEEPER_WAIT" = "true" ]; then
  success="false"
  for i in $(seq "$ZOOKEEPER_WAIT_RETRIES"); do
    if ok=$(echo ruok | nc "$first_zk" "$STORM_ZOOKEEPER_PORT" -w "$ZOOKEEPER_WAIT_TIMEOUT") && [ "$ok" = "imok" ]; then
      success="true"
      break
    else
      echo "Connect attempt $i of $ZOOKEEPER_WAIT_RETRIES failed, retrying..."
      sleep "$ZOOKEEPER_WAIT_DELAY"
    fi
  done

  if [ "$success" != "true" ]; then
    echo "Could not connect to $first_zk after $i attempts, exiting..."
    sleep 1
    exit 1
  fi
fi

---------------
extra

if [ -z "$STORM_LOCAL_HOSTNAME" ]; then
  # see also: http://stackoverflow.com/a/21336679
  ip=$(ip route get 8.8.8.8 | awk 'NR==1 {print $NF}')
  echo "Using autodetected IP as advertised hostname: $ip"
  export STORM_LOCAL_HOSTNAME=$ip
fi