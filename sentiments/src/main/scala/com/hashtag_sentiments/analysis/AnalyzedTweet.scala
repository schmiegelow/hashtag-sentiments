package com.hashtag_sentiments.analysis

import play.api.libs.json.Json

case class AnalyzedTweet(tweetId: Long, user:String, timestamp: Long, tweet: String, hashtag: String, score: Float, magnitude: Float)

object AnalyzedTweet {
  implicit val analyzedTweetFormat = Json.format[AnalyzedTweet]
}