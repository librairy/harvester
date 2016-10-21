FROM librairy/tomcat:1.0
MAINTAINER Librairy <librairy.framework@gmail.com>

ENV LIBRAIRY_HOME /librairy

WORKDIR /usr/local/tomcat

RUN mkdir $LIBRAIRY_HOME/files
RUN mkdir $LIBRAIRY_HOME/inbox
RUN mkdir $LIBRAIRY_HOME/inbox/default

ADD harvester.war /usr/local/tomcat/webapps/harvester.war

ADD waitFor.sh /
RUN chmod +x /waitFor.sh

ENV JAVA_OPTS='-Xmx4g'

VOLUME /librairy
VOLUME /usr/local/tomcat

EXPOSE 8080
CMD ["catalina.sh", "run"]
