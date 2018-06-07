package com.hashtag_sentiments.analysis

import java.util.Properties

import com.google.cloud.language.v1.Document.Type
import com.google.cloud.language.v1.{Document, LanguageServiceClient}
import com.google.cloud.translate.Translate.TranslateOption
import com.google.cloud.translate.{TranslateOptions, Translation}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}
import play.api.libs.json.Json


object SentimentAnalyzer extends LazyLogging {

  val configuration: Config = ConfigFactory.load()

  def main(args: Array[String]): Unit = {


    val bootstrapServers = if (args.length > 0) args(0) else "kafka01.internal-service:9092,kafka02.internal-service:9093,kafka03.internal-service:9094"
    val builder = new StreamsBuilder

    val streamingConfig = {
      val settings = new Properties
      settings.put(StreamsConfig.APPLICATION_ID_CONFIG, getClass.getName)
      settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
      //settings.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      // Specify default (de)serializers for record keys and for record values.
      settings.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass.getName)
      settings.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass.getName)
      settings
    }

    createTopology(builder, "tweets", "analyzed")

    val stream: KafkaStreams = new KafkaStreams(builder.build(), streamingConfig)

    sys.addShutdownHook({
      stream.close()
    })

    stream.start()

  }

  def createTopology(builder: StreamsBuilder, input: String, output: String): Unit = {
    // Read the input Kafka topic into a KStream instance.
    val tweets: KStream[Array[Byte], Array[Byte]] = builder.stream(input)

    val incomingValues: KStream[Array[Byte], Array[Byte]] = tweets
      .mapValues(value => {
        val text = new String(value)
        val tweet = Json.parse(text).as[Tweet]
        val originalLanguage = detectLanguage(tweet.tweet)
        val analyzedTweet = if (originalLanguage == "en") {
          detectSentiment(tweet)
        } else {
          detectSentiment(tweet.copy(tweet = translateText(tweet.tweet, originalLanguage).getTranslatedText))
        }
        Json.toJson(analyzedTweet).toString().getBytes
      })
    incomingValues.to(output)
  }

  def translateText(text: String, fromLanguage: String): Translation = {
    val translate = TranslateOptions.getDefaultInstance.getService
    // Translates some text into Russian
    translate.translate(text, TranslateOption.sourceLanguage(fromLanguage), TranslateOption.targetLanguage("en"))
  }

  def detectLanguage(text: String): String = {
    val translate = TranslateOptions.getDefaultInstance.getService
    translate.detect(text).getLanguage
  }

  def detectSentiment(tweet: Tweet): AnalyzedTweet = {
      val language = LanguageServiceClient.create
      try {
        val doc = Document.newBuilder.setContent(tweet.tweet).setType(Type.PLAIN_TEXT).build
        val sentiment = language.analyzeSentiment(doc).getDocumentSentiment
        println(s"Text: ${tweet.tweet}, Scores: ${sentiment.getScore}/${sentiment.getMagnitude}")
        AnalyzedTweet(tweet.tweetId, tweet.user, tweet.timestamp, tweet.tweet, tweet.hashtag, sentiment.getScore, sentiment.getMagnitude)
      } finally if (language != null) language.close()
  }
}
