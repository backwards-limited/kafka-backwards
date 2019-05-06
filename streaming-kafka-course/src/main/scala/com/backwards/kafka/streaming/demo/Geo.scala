package com.backwards.kafka.streaming.demo

import io.circe.Error
import io.circe.generic.auto._
import com.softwaremill.sttp.{DeserializationError, _}
import com.softwaremill.sttp.circe._

// TODO - Remove hard coded URL
object Geo {
  /**
    * JSON response from Geo REST API:
    * {{{
    * $ http localhost:80/city/50.180.47.38
    *   {
    *    "City":{
    *       "GeoNameID":4180439,
    *       "Names":{
    *          "de":"Atlanta",
    *          "en":"Atlanta",
    *          ...
    * }}}
    *
    * @param ip String The IP to lookup
    * @return DeserializationError[Error] Either Geo
    */
  def geo(ip: String): DeserializationError[Error] Either Geo = {
    implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

    val response: Id[Response[DeserializationError[Error] Either Geo]] = sttp
      .get(uri"http://localhost:80/city/$ip")
      .response(asJson[Geo])
      .send()

    response.unsafeBody
  }

  final case class Geo(City: City, Continent: Continent, Country: Country, Location: Location, Postal: Postal, RegisteredCountry: RegisteredCountry, RepresentedCountry: RepresentedCountry, Subdivisions: Option[Seq[Subdivision]], Traits: Traits)

  final case class City(GeoNameID: Int, Names: Option[Map[String, String]])

  final case class Continent(Code: String, GeoNameID: Int, Names: Option[Map[String, String]])

  final case class Country(GeoNameID: Int, IsoCode: String, Names: Option[Map[String, String]])

  final case class Location(Latitude: Double, Longitude: Double, MetroCode: Int, TimeZone: String)

  final case class Postal(Code: String)

  final case class RegisteredCountry(GeoNameID: Int, IsoCode: String, Names: Option[Map[String, String]])

  final case class RepresentedCountry(GeoNameID: Int, IsoCode: String, Names: Option[Map[String, String]], Type: String)

  final case class Subdivision(GeoNameID: Int, IsoCode: String, Names: Option[Map[String, String]])

  final case class Traits(IsAnonymousProxy: Boolean, IsSatelliteProvider: Boolean)
}