FROM tomcat:8-jre8
MAINTAINER Librairy <librairy.framework@gmail.com>

ENV LIBRAIRY_HOME /librairy
RUN mkdir $LIBRAIRY_HOME

WORKDIR /usr/local/tomcat

RUN mkdir $LIBRAIRY_HOME/files
RUN mkdir $LIBRAIRY_HOME/files/default
RUN mkdir $LIBRAIRY_HOME/files/custom
RUN mkdir $LIBRAIRY_HOME/files/serialized
RUN mkdir $LIBRAIRY_HOME/files/collected

ADD harvester.war /usr/local/tomcat/webapps/harvester.war

ADD waitFor.sh /
RUN chmod +x /waitFor.sh

ENV JAVA_OPTS='-Xmx4g'

VOLUME /librairy
VOLUME /usr/local/tomcat

EXPOSE 8080
CMD ["catalina.sh", "run"]
