package com.hashtag_sentiments.feeder;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

public class TweetTest {

    @Test
    void shouldSerialseAndDeserialiseATweet() {
        // Given
        Tweet aTweet = new Tweet("barack", System.currentTimeMillis(), "I should have stayed president");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(aTweet);
        System.out.println(json);
        assertTrue(mapper.readValue(json, Tweet.class), aTweet);
    }

}