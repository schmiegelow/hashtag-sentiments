#!/usr/bin/env bash

curl -X POST -H "Content-Type: application/json" \
http://localhost:8083/connectors \
  -d '{
 "name": "elasticsearch-sink",
  "config": {
    "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
    "tasks.max": "1",
    "topics": "analyzed",
    "key.ignore": "true",
    "connection.url": "http://elasticsearch:9200",
    "type.name": "kafka-connect",
    "name": "elasticsearch-sink"
  }
}'