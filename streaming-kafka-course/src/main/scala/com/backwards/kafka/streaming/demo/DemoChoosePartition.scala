package com.backwards.kafka.streaming.demo

import java.util.concurrent.TimeUnit.SECONDS
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.language.postfixOps
import io.circe
import org.apache.kafka.clients.CommonClientConfigs.CLIENT_ID_CONFIG
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import com.backwards.collection.MapOps._
import com.backwards.kafka.streaming.Config._
import com.backwards.text.StringOps._
import com.backwards.time.DurationOps._
import com.typesafe.scalalogging.LazyLogging

trait DemoChoosePartition extends App with LazyLogging {
  val topic: String = "demo-choose-partition"

  val clientId: String =
    lowerKebab(getClass.getSimpleName.replaceAll("\\$", ""))

  val kafkaProps = {
    val kafkaProps: Map[String, String] =
      load[Map[String, String]]("kafka") + (CLIENT_ID_CONFIG -> clientId)

    val admin: AdminClient = AdminClient create kafkaProps
    admin createTopics asJavaCollection(Seq(new NewTopic(topic, 4, 1)))

    kafkaProps
  }
}

abstract class ConsumerDemoChoosePartition extends DemoChoosePartition {
  val consumerProps = kafkaProps + (GROUP_ID_CONFIG -> "1")

  val consumer = new KafkaConsumer[Nothing, String](consumerProps)
  sys addShutdownHook consumer.close
  consumer subscribe asJavaCollection(Seq(topic))

  while (true) {
    (consumer poll 10.seconds).iterator.asScala.foreach(println)
  }
}

object ConsumerDemoChoosePartition1 extends ConsumerDemoChoosePartition

object ConsumerDemoChoosePartition2 extends ConsumerDemoChoosePartition

object ProducerDemoChoosePartition extends DemoChoosePartition {
  val producer = new KafkaProducer[String, String](kafkaProps)
  sys addShutdownHook producer.close

  while (true) {
    val record = new ProducerRecord[String, String](topic, randomIp, randomString(10))
    println(s"Producing: $record")
    blah(randomIp)
    producer send record
    SECONDS sleep 10
  }

  def blah(ip: String) = {
    import com.softwaremill.sttp._
    import com.softwaremill.sttp.circe._
    import io.circe.generic.auto._

    final case class Geo(City: City)

    final case class City(GeoNameID: Int, Names: Map[String, String])

    /*
    $ curl http://convox.local/city/50.180.47.38
{"City":{"GeoNameID":4180439,"Names":{"de":"Atlanta","en":"Atlanta","es":"Atlanta","fr":"Atlanta","ja":"アトランタ","pt-BR":"Atlanta","ru":"Атланта","zh-CN":"亚特兰大"}},"Continent":{"Code":"NA","GeoNameID":6255149,"Names":{"de":"Nordamerika","en":"North America","es":"Norteamérica","fr":"Amérique du Nord","ja":"北アメリカ","pt-BR":"América do Norte","ru":"Северная Америка","zh-CN":"北美洲"}},"Country":{"GeoNameID":6252001,"IsoCode":"US","Names":{"de":"USA","en":"United States","es":"Estados Unidos","fr":"États-Unis","ja":"アメリカ合衆国","pt-BR":"Estados Unidos","ru":"США","zh-CN":"美国"}},"Location":{"Latitude":33.7884,"Longitude":-84.3491,"MetroCode":524,"TimeZone":"America/New_York"},"Postal":{"Code":"30306"},"RegisteredCountry":{"GeoNameID":6252001,"IsoCode":"US","Names":{"de":"USA","en":"United States","es":"Estados Unidos","fr":"États-Unis","ja":"アメリカ合衆国","pt-BR":"Estados Unidos","ru":"США","zh-CN":"美国"}},"RepresentedCountry":{"GeoNameID":0,"IsoCode":"","Names":null,"Type":""},"Subdivisions":[{"GeoNameID":4197000,"IsoCode":"GA","Names":{"en":"Georgia","es":"Georgia","fr":"Géorgie","ja":"ジョージア州","pt-BR":"Geórgia","ru":"Джорджия"}}],"Traits":{"IsAnonymousProxy":false,"IsSatelliteProvider":false}}
     */

    implicit val backend = HttpURLConnectionBackend()

    val response: Id[Response[DeserializationError[io.circe.Error] Either Geo]] = sttp
      .get(uri"http://localhost:80/city/$ip")
      .response(asJson[Geo])
      .send()

    println(s"===> response = $response")


    // response = Response(Right(Right(Geo(City(1816670,Map(fr -> Pékin, zh-CN -> 北京, pt-BR -> Pequim, de -> Peking, ru -> Пекин, en -> Beijing, es -> Pekín, ja -> 北京市))))),200,OK,Vector((Content-Length,1046), (Date,Sun, 05 May 2019 22:23:27 GMT), (Content-Type,text/plain; charset=utf-8)),List())
  }
}