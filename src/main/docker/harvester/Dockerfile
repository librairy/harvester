FROM tomcat:8.0
MAINTAINER Carlos Badenes <cbadenes@gmail.com>

WORKDIR /usr/local/tomcat

ENV COLUMN_DB_HOST column-db
ENV DOCUMENT_DB_HOST document-db
ENV GRAPH_DB_HOST graph-db
ENV MESSAGE_BROKER_HOST eventbus

ENV LIBRAIRY_HOME /usr/local/librairy

ENV TEXT_MINER_URL http://backingdata.org/dri/library/1.2/DRIresources-1.2.tar.gz

RUN mkdir $LIBRAIRY_HOME
RUN mkdir $LIBRAIRY_HOME/workspace
RUN mkdir $LIBRAIRY_HOME/serialized

RUN curl "$TEXT_MINER_URL" | tar -C /usr/local -zx

ADD harvester.war /usr/local/tomcat/webapps/harvester.war

VOLUME $LIBRAIRY_HOME

EXPOSE 8080
CMD ["catalina.sh", "run"]