package com.hashtag_sentiments.analysis

case class AnalyzedTweet(user:String, timestamp: Long, tweet: String, score: Float, magnitude: Float)