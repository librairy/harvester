# Librairy Harvester - Files [![Build Status](https://travis-ci.org/librairy/harvester-file.svg?branch=develop)](https://travis-ci.org/librairy/harvester-file)

Collect and process local files

## Architecture

...

## Get Start!

The only prerequisite to consider is to install [Docker](https://www.docker.com/) in your system.

Then, run `docker-compose up` by using this file `docker-compose.yml`:  

```yml
ftp:
  container_name: ftp
  image: librairy/ftp:1.0
  restart: always
  ports:
    - "5051:21"
  volumes:
    - ./data:/home/ftpusers/librairy
harvester:
  container_name: harvester
  image: librairy/harvester
  restart: always
  volumes:
    - ./data:/librairy/files/uploaded
  links:
      - column-db
      - document-db
      - graph-db
      - event-bus
```

That's all!! **librairy explorer** should be run in your system now!

Instead of deploy all containers as a whole, you can deploy each of them independently. It is useful to run the service distributed in several host-machines.

## FTP Server

```sh
docker run -it --rm --name ftp -p 5051:21 -v /Users/cbadenes/Downloads/ftp:/librairy/files/uploaded librairy/ftp:1.0
```

Remember that, by using the flags: `-it --rm`, the services runs in foreground mode, if you want to deploy it in background mode,  or even as a domain service, you should use: `-d --restart=always`

## Document-oriented Database

```sh
docker run -it --rm --name document-db -p 5020:9200 -p 5021:9300 librairy/document-db:1.0
```

## Graph-oriented Database

```sh
docker run -it --rm --name graph-db -p 5030:7474 librairy/graph-db:1.0
```

## Message Broker

```sh
docker run -it --rm --name event-bus -p 5040:15672 -p 5041:5672 librairy/event-bus:1.0
```

## Explorer

```sh
docker run -it --rm --name explorer -p 8080:8080 --link column-db --link document-db --link graph-db --link event-bus librairy/explorer
```
