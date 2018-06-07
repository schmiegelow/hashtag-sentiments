
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

public class TwitterFeeder {

    private static final String TWITTER_CONSUMER_KEY = "vAgz0PKSnfbACF1qc4Hkix1wl";
    private static final String TWITTER_CONSUMER_SECRET = "eU9ojZK5m82DmA0N2PXowJwWSpHdU6fulDFIfb8KcZIUOQvMwo";
    private static final String TWITTER_ACCESS_TOKEN = "128680668-8CiBfbx7ci8euar5eGM60VabBPZC1234ecjTOjzh";
    private static final String TWITTER_ACCESS_TOKEN_SECRET = "aFJl8lGzN4oanwMEIh49hIZXQALBUqE7P5OH4xDSC5oVn";

    private Twitter twitter;

    public static void main(String[] args) {

    }


    public TwitterFeeder() {
        getConfigurationBuildObject();
        TwitterFactory twitterFactory = new TwitterFactory(getConfigurationBuildObject().build());
        twitter = twitterFactory.getInstance();
    }

    private ConfigurationBuilder getConfigurationBuildObject() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
        configurationBuilder.setOAuthAccessToken(TWITTER_ACCESS_TOKEN);
        configurationBuilder.setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);
        return configurationBuilder;
    }

    public List<Status> getTweets(String hashtag) {
        List<Status> tweets = new ArrayList<Status>();
        Query query = new Query(hashtag);
        try {
            QueryResult queryResult = twitter.search(query);
            tweets = queryResult.getTweets();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return tweets;
    }

}