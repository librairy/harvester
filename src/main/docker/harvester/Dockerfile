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

ENV COLUMN_DB_HOST column-db
ENV COLUMN_DB_PORT 9042

ENV DOCUMENT_DB_HOST document-db
ENV DOCUMENT_DB_PORT 9300

ENV GRAPH_DB_HOST graph-db
ENV GRAPH_DB_PORT 7474

ENV MESSAGE_BROKER_HOST event-bus
ENV MESSAGE_BROKER_PORT 5672

VOLUME /librairy
VOLUME /usr/local/tomcat

EXPOSE 8080
CMD ["catalina.sh", "run"]