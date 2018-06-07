import com.hashtag_sentiments.feeder.Tweet;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class TwitterFeeder {

    private static final String TWITTER_CONSUMER_KEY = "vAgz0PKSnfbACF1qc4Hkix1wl";
    private static final String TWITTER_CONSUMER_SECRET = "eU9ojZK5m82DmA0N2PXowJwWSpHdU6fulDFIfb8KcZIUOQvMwo";
    private static final String TWITTER_ACCESS_TOKEN = "128680668-8CiBfbx7ci8euar5eGM60VabBPZC1234ecjTOjzh";
    private static final String TWITTER_ACCESS_TOKEN_SECRET = "aFJl8lGzN4oanwMEIh49hIZXQALBUqE7P5OH4xDSC5oVn";

    private Twitter twitter;

    public TwitterFeeder() {
        getConfigurationBuildObject();
        TwitterFactory twitterFactory = new TwitterFactory(getConfigurationBuildObject().build());
        twitter = twitterFactory.getInstance();
    }

    public static void main(String[] args) {

        try {
            new TwitterFeeder().getTweets("MeToo");
        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

    private ConfigurationBuilder getConfigurationBuildObject() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
        configurationBuilder.setOAuthAccessToken(TWITTER_ACCESS_TOKEN);
        configurationBuilder.setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);
        return configurationBuilder;
    }

    private List<Tweet> getTweets(String hashtag) throws TwitterException {
        Query query = new Query(hashtag);
            QueryResult queryResult = twitter.search(query);
            return queryResult
                    .getTweets()
                    .stream()
                    .map(status -> new Tweet(status.getUser().getName(), status.getCreatedAt().getTime(), status.getText()))
                    .collect(Collectors.toList()));
    }

}