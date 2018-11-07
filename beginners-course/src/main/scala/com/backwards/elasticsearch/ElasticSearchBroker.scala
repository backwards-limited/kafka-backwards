package com.backwards.elasticsearch

import scala.concurrent.Future
import scala.language.postfixOps
import org.apache.http.HttpHost
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.{RestClient, RestClientBuilder}
import org.json4s.Writer
import org.json4s.jackson.JsonMethods._
import com.backwards.config.elasticSearchConfig
import com.backwards.logging.Logging
import com.sksamuel.elastic4s.IndexAndType
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.index.CreateIndexResponse
import com.sksamuel.elastic4s.http.{ElasticClient, Response}
import com.sksamuel.elastic4s.indexes.IndexRequest

/**
  * For simplicity/demonstration we block on calls to the underlying Elasticsearch client.
  */
class ElasticSearchBroker extends Logging {
  val httpClientConfigCallback: RestClientBuilder.HttpClientConfigCallback = (httpClientBuilder: HttpAsyncClientBuilder) => {
    val credentialsProvider = new BasicCredentialsProvider
    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticSearchConfig.credentials.user, elasticSearchConfig.credentials.password))
    httpClientBuilder setDefaultCredentialsProvider credentialsProvider
  }

  val httpHosts: Seq[HttpHost] = elasticSearchConfig.bootstrap.servers.map(_.toJavaURI).map { uri =>
    new HttpHost(uri.getHost, uri.getPort, uri.getScheme)
  }

  val restClientBuilder: RestClientBuilder = RestClient.builder(httpHosts: _*).setHttpClientConfigCallback(httpClientConfigCallback)

  val client: ElasticClient = {
    val client = ElasticClient fromRestClient restClientBuilder.build
    sys addShutdownHook client.close()

    client
  }

  def index(name: String): Future[Response[CreateIndexResponse]] = {
    info(s"Creating index: $name")

    client.execute {
      createIndex("twitter")
    }
  }

  def documentEach[V: Writer](indexAndType: IndexAndType)(xs: Seq[(String, V)]): Unit =
    xs.foreach { case (key, value) =>
      val response = client.execute(
        indexRequest(indexAndType)(key -> value)
      ).await

      info(s"Elasticsearch response = ${response.result.id}")
    }

  def documentBulk[V: Writer](indexAndType: IndexAndType)(xs: Seq[(String, V)]): Unit = {
    val response = client.execute(
      bulk(xs map indexRequest(indexAndType))
    ).await

    info(s"Elasticsearch response = ${response.result.items.map(_.id).mkString(", ")}")
  }

  def indexRequest[V: Writer](indexAndType: IndexAndType)(keyValue: (String, V)): IndexRequest = keyValue match {
    case (key, value) => indexInto(indexAndType) id key doc pretty(render(implicitly[Writer[V]].write(value)))
  }
}