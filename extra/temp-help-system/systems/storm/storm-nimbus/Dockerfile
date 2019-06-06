FROM sunside/storm:latest
MAINTAINER Markus Mayer <widemeadows@gmail.com>

RUN /usr/bin/config-supervisord.sh nimbus 
RUN /usr/bin/config-supervisord.sh drpc

EXPOSE 3772 3773 6627 

#added
ADD start-supervisor.sh /usr/bin/start-supervisor.sh

CMD /usr/bin/start-supervisord.sh
