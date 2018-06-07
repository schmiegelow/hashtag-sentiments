package com.hashtag_sentiments.analysis

import play.api.libs.json.Json

case class Tweet(tweetId: Long, user:String, timestamp: Long, tweet: String, hashtag: String)

object Tweet {
  implicit val tweetFormat = Json.format[Tweet]
}