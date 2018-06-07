package com.hashtag_sentiments.feeder;

import java.util.Objects;

public class Tweet {
    private final String username;
    private final long timestamp;
    private final String tweet;

    public Tweet(String username, long timestamp, String tweet) {
        this.username = username;
        this.timestamp = timestamp;
        this.tweet = tweet;
    }


    public String getUsername() {
        return username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTweet() {
        return tweet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet1 = (Tweet) o;
        return timestamp == tweet1.timestamp &&
                Objects.equals(username, tweet1.username) &&
                Objects.equals(tweet, tweet1.tweet);
    }

    @Override
    public int hashCode() {

        return Objects.hash(username, timestamp, tweet);
    }
}
