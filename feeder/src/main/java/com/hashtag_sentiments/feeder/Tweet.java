package com.hashtag_sentiments.feeder;

import java.io.Serializable;
import java.util.Objects;

public class Tweet implements Serializable {
    private final long tweetId;
    private final String user;
    private final long timestamp;
    private final String tweet;
    private final String hashtag;

    public Tweet(long tweetId, String user, long timestamp, String tweet, String hashtag) {
        this.tweetId = tweetId;
        this.user = user;
        this.timestamp = timestamp;
        this.tweet = tweet;
        this.hashtag = hashtag;
    }

    public long getTweetId() {
        return tweetId;
    }

    public String getUser() {
        return user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTweet() {
        return tweet;
    }

    public String getHashtag() { return hashtag; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet1 = (Tweet) o;
        return timestamp == tweet1.timestamp &&
                Objects.equals(user, tweet1.user) &&
                Objects.equals(tweet, tweet1.tweet) &&
                Objects.equals(tweetId, tweet1.tweetId) &&
                Objects.equals(hashtag, tweet1.hashtag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tweetId, user, timestamp, tweet, hashtag);
    }
}