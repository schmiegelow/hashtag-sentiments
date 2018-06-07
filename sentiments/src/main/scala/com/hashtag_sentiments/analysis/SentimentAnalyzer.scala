package com.hashtag_sentiments.analysis

import java.util.Properties

import com.google.cloud.translate.Translate.TranslateOption
import com.google.cloud.translate.{TranslateOptions, Translation}
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}
import org.apache.kafka.streams.kstream.KStream

import com.google.cloud.language.v1.Document
import com.google.cloud.language.v1.Document.Type
import com.google.cloud.language.v1.LanguageServiceClient
import com.google.cloud.language.v1.Sentiment


class SentimentAnalyzer extends LazyLogging {

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
    val textLines: KStream[Array[Byte], Array[Byte]] = builder.stream(input)

    val incomingValues: KStream[Array[Byte], Array[Byte]] = textLines
      .filter((_: Array[Byte], value: Array[Byte]) => {
        detectLanguage(new String(value)) != "en"
      })
      .mapValues(value => {
        val text = new String(value)
        val fromLanguage = detectLanguage(text)
        logger.info(s"Translating $text in $fromLanguage to English")
        translateText(text, fromLanguage).getTranslatedText.getBytes()
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

  def detectSentiment(text: String) = {
      val language = LanguageServiceClient.create
      try { // The text to analyze
        val text = "Hello, world!"
        val doc = Document.newBuilder.setContent(text).setType(Type.PLAIN_TEXT).build
        // Detects the sentiment of the text
        val sentiment = language.analyzeSentiment(doc).getDocumentSentiment
        System.out.printf("Text: %s%n", text)
//        System.out.printf("Sentiment: %s, %s%n", sentiment.getScore, sentiment.getMagnitude)
      } finally if (language != null) language.close()
  }
}
