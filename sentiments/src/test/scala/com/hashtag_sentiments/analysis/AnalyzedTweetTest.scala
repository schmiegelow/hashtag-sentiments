package com.hashtag_sentiments.analysis

import org.scalatest.FunSpec
import play.api.libs.json.Json

class AnalyzedTweetTest extends FunSpec {

  describe("AnalyzedTweetTest") {

    it("should analyse a neutral tweet") {
      val analyzedTweet = AnalyzedTweet(123456, "obama", System.currentTimeMillis(), "I should have stayed on", "#Metoo", 0.9f, 0.1f)
      val json =Json.toJson(analyzedTweet).toString()
      println(json)
      assert(analyzedTweet == Json.parse(json).as[AnalyzedTweet])
    }
  }
}
