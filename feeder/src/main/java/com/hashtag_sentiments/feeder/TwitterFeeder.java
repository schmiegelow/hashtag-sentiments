package com.hashtag_sentiments.feeder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hashtag_sentiments.producer.KafkaProducer;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class TwitterFeeder {

    private static final String TWITTER_CONSUMER_KEY = "vAgz0PKSnfbACF1qc4Hkix1wl";
    private static final String TWITTER_CONSUMER_SECRET = "eU9ojZK5m82DmA0N2PXowJwWSpHdU6fulDFIfb8KcZIUOQvMwo";
    private static final String TWITTER_ACCESS_TOKEN = "128680668-8CiBfbx7ci8euar5eGM60VabBPZC1234ecjTOjzh";
    private static final String TWITTER_ACCESS_TOKEN_SECRET = "aFJl8lGzN4oanwMEIh49hIZXQALBUqE7P5OH4xDSC5oVn";
    private static final String HASHTAG_PREFIX = "#";

    private Twitter twitter;
    private String hashtag;
    private long lastCreatedDate;


    public TwitterFeeder() {
        getConfigurationBuildObject();
        TwitterFactory twitterFactory = new TwitterFactory(getConfigurationBuildObject().build());
        twitter = twitterFactory.getInstance();
    }

    public static void main(String[] args) {
        String hashtag = args[0];

        TwitterFeeder twitterFeeder = new TwitterFeeder();
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(twitterFeeder.getPropertiesConfig());
        ObjectMapper mapper = new ObjectMapper();
        while (true) {
            try {
                System.out.println(String.format("Fetching tweets for %s at %s", hashtag, new Date()));
                List<Tweet> tweets;
                tweets = twitterFeeder.getFilteredTweets(twitterFeeder.getTweets(hashtag));
                tweets.forEach(tweet -> {
                    try {
                        System.out.println(String.format("Sending tweet %s at %s", tweet.getTweetId(), new Date()));
                        kafkaProducer.send("tweets", String.valueOf(tweet.getTweetId()), mapper.writeValueAsString(tweet));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }});
                Thread.sleep(10000);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private List<Tweet> getFilteredTweets(List<Tweet> tweets) {
        tweets = tweets.stream().filter(t -> t.getTimestamp() > (lastCreatedDate)).collect(Collectors.toList());
        Optional<Tweet> lastFetchedTweet = tweets.stream().max(Comparator.comparing(Tweet::getTimestamp));
        lastCreatedDate = lastFetchedTweet.map(Tweet::getTimestamp).orElse(lastCreatedDate);
        tweets.forEach(t -> System.out.println(t.getTimestamp()));
        return tweets;
    }

    private Properties getPropertiesConfig() {
        Properties props = new Properties();
        int numInputMessages = 100;
        props.put("bootstrap.servers", "kafka01.internal-service:9092");
        props.put("acks", "all");
        props.put("batch.size", numInputMessages);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("group.id", this.getClass().getName());
        return props;
    }

    private ConfigurationBuilder getConfigurationBuildObject() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
        configurationBuilder.setOAuthAccessToken(TWITTER_ACCESS_TOKEN);
        configurationBuilder.setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);
        return configurationBuilder;
    }

    private List<Tweet> getTweets(String inputHashtag) throws TwitterException {
        hashtag = HASHTAG_PREFIX + inputHashtag;
        Query query = new Query(hashtag);
        QueryResult queryResult = twitter.search(query);
        return queryResult
                .getTweets()
                .stream()
                .map(status -> new Tweet(status.getId(), status.getUser().getName(), status.getCreatedAt().getTime(), status.getText(), hashtag))
                .collect(Collectors.toList());
    }
}