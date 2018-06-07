# hashtag-sentiments
A prototype docker network that analyses tweets from a hashtag using Google Sentiment Analysis API

## To build the project, run:

- ```sbt package```
- ```sbt docker:stage```

This will create all local images that then run in the mini cluster managed by docker-compose

## To start a mini cluster on your machine, you'll need docker-compose and docker. just run

```docker-compose up```

to start the cluster.

Exec into a kafka node and run

```kafka-topics --create --if-not-exists --zookeeper zookeeper.internal-service:2181 --replication-factor 3 --partitions 1 --topic tweets```

```kafka-topics --create --if-not-exists --zookeeper zookeeper.internal-service:2181 --replication-factor 3 --partitions 1 --topic analyzed```

Then run:

```./kafka-connect/bin/setup.sh```

After a while, once all services have started, you should see the first results streaming into your Elastic Search instance.