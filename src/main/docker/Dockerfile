FROM librairy/tomcat:1.0
MAINTAINER Librairy <librairy.framework@gmail.com>

ENV LIBRAIRY_HOME /librairy

RUN mkdir $LIBRAIRY_HOME/files
RUN mkdir $LIBRAIRY_HOME/inbox

WORKDIR /usr/local/tomcat

ADD harvester.war /usr/local/tomcat/webapps/harvester.war

#ENV JAVA_OPTS='-Xmx4g'

VOLUME /librairy
VOLUME /usr/local/tomcat

EXPOSE 8080
CMD ["catalina.sh", "run"]
