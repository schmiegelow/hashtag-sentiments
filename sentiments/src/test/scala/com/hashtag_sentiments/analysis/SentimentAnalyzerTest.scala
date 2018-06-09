package com.hashtag_sentiments.analysis

import com.hashtag_sentiments.analysis.SentimentAnalyzer.{detectLanguage, detectSentiment, translateText}
import org.scalatest.FunSpec
import play.api.libs.json.Json

class SentimentAnalyzerTest extends FunSpec {

  ignore("Analyses sentiments in tweets") {
    it("takes a tweet and computes the sentiment") {
      val json = "{\"tweetId\":123456,\"user\":\"obama\",\"timestamp\":1528383912394,\"tweet\":\"I should have stayed on\",\"hashtag\":\"#Metoo\"}"
      val tweet = Json.parse(json).as[Tweet]
      println(SentimentAnalyzer.detectSentiment(tweet))
    }
  }

  it("should analyse a negative tweet") {
    val analyzedTweet = AnalyzedTweet(123456, "Sad Server", System.currentTimeMillis(), "This is literrally the worst crap I've ever seen", "#Metoo", 0.9f, 0.1f)
    val json =Json.toJson(analyzedTweet).toString()
    val tweet = Json.parse(json).as[Tweet]
    println(SentimentAnalyzer.detectSentiment(tweet))
  }

  it("should analyse a postive tweet") {
    val analyzedTweet = AnalyzedTweet(123456, "Mr Magic Lemon", System.currentTimeMillis(), "This is absolutely lovely, fantastic", "#Metoo", 0.9f, 0.1f)
    val json =Json.toJson(analyzedTweet).toString()
    val tweet = Json.parse(json).as[Tweet]
    println(SentimentAnalyzer.detectSentiment(tweet))
  }

  it("should analyse a non english text tweet") {
    val analyzedTweet = AnalyzedTweet(123456, "Mr Magic Lemon", System.currentTimeMillis(), "Das ist mal ein Ding!", "#Metoo", 0.9f, 0.1f)
    val json =Json.toJson(analyzedTweet).toString()
    val tweet = Json.parse(json).as[Tweet]
    val originalLanguage = detectLanguage(tweet.tweet)
    val analyzedTweeted = if (originalLanguage == "en") {
      detectSentiment(tweet)
    } else {
      detectSentiment(tweet.copy(tweet = translateText(tweet.tweet, originalLanguage).getTranslatedText))
    }
    println(Json.toJson(analyzedTweeted).toString())
  }

}
