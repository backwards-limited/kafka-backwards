package com.backwards.elasticsearch

import java.util.concurrent.TimeUnit
import scala.language.postfixOps
import org.apache.http.HttpHost
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.{RestClient, RestClientBuilder}
import com.backwards.config.elasticSearchConfig
import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.search.SearchResponse
import com.sksamuel.elastic4s.http.{bulk => _, search => _, _}

class ElasticSearchBroker {
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

  def blah() = {
    println(s"================================> ELASTIC SLEEPING.......... this is ridiculous, but without it we get a connection refused exception")
    //TimeUnit.SECONDS.sleep(30)

    ///////////////////////////////////////// TODO - Remove below
    /*client.execute {
      createIndex("artists").mappings(
        mapping("modern").fields(
          textField("name")
        )
      )
    }.await

    // Next we index a single document which is just the name of an Artist.
    // The RefreshPolicy.Immediate means that we want this document to flush to the disk immediately.
    // see the section on Eventual Consistency.
    client.execute {
      indexInto("artists" / "modern").fields("name" -> "L.S. Lowry").refresh(RefreshPolicy.Immediate)
    }.await

    // now we can search for the document we just indexed
    val resp = client.execute {
      search("artists") query "lowry"
    }.await

    // resp is a Response[+U] ADT consisting of either a RequestFailure containing the
    // Elasticsearch error details, or a RequestSuccess[U] that depends on the type of request.
    // In this case it is a RequestSuccess[SearchResponse]

    println("---- Search Results ----")
    resp match {
      case failure: RequestFailure => println("We failed " + failure.error)
      case results: RequestSuccess[SearchResponse] => println(results.result.hits.hits.toList)
      case results: RequestSuccess[_] => println(results.result)
    }

    // Response also supports familiar combinators like map / flatMap / foreach:
    resp foreach (search => println(s"There were ${search.totalHits} total hits"))*/
  }
}