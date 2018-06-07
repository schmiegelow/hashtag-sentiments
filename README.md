# hashtag-sentiments
A prototype docker network that analyses tweets from a hashtag using Google Sentiment Analysis API

## To start a mini cluster on your machine, you'll need docker-compose and docker. just run

```docker-compose up```

to start the cluster.

Exec into a kafka node and run

```kafka-topics --create --if-not-exists --zookeeper zookeeper.internal-service:2181 --replication-factor 3 --partitions 1 --topic tweets```

```kafka-topics --create --if-not-exists --zookeeper zookeeper.internal-service:2181 --replication-factor 3 --partitions 1 --topic analyzed```

Then run:

```./kafka-connect/bin/setup.sh```