package com.hashtag_sentiments.analysis

import org.scalatest.FunSpec
import play.api.libs.json.Json

class SentimentAnalyzerTest extends FunSpec {

  ignore("Analyses sentiments in tweets") {
    it("takes a tweet and computes the sentiment") {
      val json = "{\"tweetId\":123456,\"user\":\"obama\",\"timestamp\":1528383912394,\"tweet\":\"I should have stayed on\",\"hashtag\":\"#Metoo\"}"
      val tweet = Json.parse(json).as[Tweet]
      println(new SentimentAnalyzer().detectSentiment(tweet))
    }
  }

  it("should analyse a negative tweet") {
    val analyzedTweet = AnalyzedTweet(123456, "Sad Server", System.currentTimeMillis(), "This is literrally the worst crap I've ever seen", "#Metoo", 0.9f, 0.1f)
    val json =Json.toJson(analyzedTweet).toString()
    val tweet = Json.parse(json).as[Tweet]
    println(new SentimentAnalyzer().detectSentiment(tweet))
  }

  it("should analyse a postive tweet") {
    val analyzedTweet = AnalyzedTweet(123456, "Mr Magic Lemon", System.currentTimeMillis(), "This is absolutely lovely, fantastic", "#Metoo", 0.9f, 0.1f)
    val json =Json.toJson(analyzedTweet).toString()
    val tweet = Json.parse(json).as[Tweet]
    println(new SentimentAnalyzer().detectSentiment(tweet))
  }


}
