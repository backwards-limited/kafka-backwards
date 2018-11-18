package com.backwards.twitter.stream

import cats.data.NonEmptyList
import cats.effect.IO
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.streams.StreamsConfig._
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder}
import com.backwards.{BackwardsApp, kafka}
import com.backwards.transform.Transform
import com.backwards.twitter.{Json, TweetSerde}
import com.backwards.twitter.simple.{TwitterBroker, TwitterProducer}
import com.danielasfregola.twitter4s.entities.Tweet

/**
  * Demo application which shows the following:
  *   - Query Twitter for "scala" and other tweets using [[https://github.com/DanielaSfregola/twitter4s twitter4s]]
  *   - Send tweets to Kafka using [[com.backwards.kafka.Producer Producer]]
  *   - Consume tweets via Kafka Streams which:
  *     - filter tweets with high follower count
  *     - produce back to another topic for other consumers
  *
  * Note that Kafka Streams "application ID" can be thought of as similar to Kafka Consumer "group ID"
  */
object TwitterRunner extends BackwardsApp with Transform with Json {
  val topic = "twitter-topic"

  val twitterProducer = TwitterProducer[IO](topic)

  val twitterBroker = new TwitterBroker
  twitterBroker.track(NonEmptyList.of("scala", "bitcoin"))(twitterProducer.produce(_).unsafeRunSync)

  val streamsConfig =
    kafka.config +
    (APPLICATION_ID_CONFIG -> "streamer") +
    (DEFAULT_KEY_SERDE_CLASS_CONFIG -> classOf[StringSerde].getName) +
    (DEFAULT_VALUE_SERDE_CLASS_CONFIG -> TweetSerde().getClass.getName)

  val streamsBuilder = new StreamsBuilder()

  streamsBuilder.stream[String, Tweet](topic)
    .filter { (_, tweet) => tweet.user.exists(_.followers_count > 10000) }
    .to("important-tweets-topic")

  val kafkaStreams = new KafkaStreams(streamsBuilder.build(), streamsConfig)
  kafkaStreams.start()
}