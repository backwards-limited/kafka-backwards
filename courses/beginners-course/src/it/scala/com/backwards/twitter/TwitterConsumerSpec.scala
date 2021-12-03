package com.backwards.twitter

import java.time.Instant
import java.{lang, util}
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.language.postfixOps
import better.files.{File, Resource}
import cats.Id
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import io.lemonlabs.uri.Uri
import org.apache.kafka.clients.consumer.{ConsumerRecord, MockConsumer, OffsetResetStrategy}
import org.apache.kafka.common.TopicPartition
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.backwards.config.BootstrapConfig
import com.backwards.docker.DockerCompose.ServiceName
import com.backwards.docker.{DockerCompose, DockerComposeFixture}
import com.backwards.kafka.{Consumer, KafkaConfig}
import com.backwards.transform.Transform
import com.backwards.twitter.simple.TwitterConsumer
import com.danielasfregola.twitter4s.entities.Tweet

class TwitterConsumerSpec extends AnyWordSpec with Matchers with ScalaFutures with Transform with DockerComposeFixture {
  override implicit def patienceConfig: PatienceConfig = PatienceConfig(10 seconds, 2 seconds)

  val dockerCompose: DockerCompose =
    DockerCompose("kafka", Seq(File(Resource.getUrl("docker-compose.yml"))))

  lazy val kafkaPort: Int = dockerCompose.containerMappedPort(ServiceName("kafka"), 9092)

  implicit lazy val config: KafkaConfig = KafkaConfig(BootstrapConfig(Seq(Uri.parse(s"http://localhost:$kafkaPort"))))

  trait Context {
    val topic = "topic"
    val tweet: Tweet = Tweet(created_at = Instant.now, id = 6, id_str = "blah", source = "blahblah", text = "something")

    val topicPartition = new TopicPartition(topic, 0)

    val mockConsumer = new MockConsumer[String, Tweet](OffsetResetStrategy.EARLIEST)

    mockConsumer.assign(util.Arrays.asList(topicPartition))

    val beginningOffsets: Map[TopicPartition, lang.Long] =
      Map(topicPartition -> java.lang.Long.valueOf(0))

    mockConsumer.updateBeginningOffsets(beginningOffsets.asJava)
  }

  "Twitter consumer" should {
    "receive tweets within Id effect" in new Context {
      val consumer: TwitterConsumer[Id] = new TwitterConsumer[Id](topic) {
        override val consumer: Consumer[Id, String, Tweet] = new Consumer[Id, String, Tweet](topic, mockConsumer)
      }

      val key1 = "key1"
      mockConsumer addRecord new ConsumerRecord[String, Tweet](topic, 0, 0, key1, tweet)

      consumer.consume mustEqual Seq(key1 -> tweet)

      val key2 = "key2"
      mockConsumer addRecord new ConsumerRecord[String, Tweet](topic, 0, 1, key2, tweet)

      consumer.consume mustEqual Seq(key2 -> tweet)
    }

    "receive tweets within IO effect - duplicate of previous example within Id effect for illustration purposes" in new Context {
      val consumer: TwitterConsumer[IO] = new TwitterConsumer[IO](topic) {
        override val consumer: Consumer[IO, String, Tweet] = new Consumer[IO, String, Tweet](topic, mockConsumer)
      }

      val key1 = "key1"
      mockConsumer addRecord new ConsumerRecord[String, Tweet](topic, 0, 0, key1, tweet)

      consumer.consume.unsafeRunSync.toList mustEqual Seq(key1 -> tweet)

      val key2 = "key2"
      mockConsumer addRecord new ConsumerRecord[String, Tweet](topic, 0, 1, key2, tweet)

      consumer.consume.unsafeRunSync.toList mustEqual Seq(key2 -> tweet)
    }
  }
}