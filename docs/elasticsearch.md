# Elasticsearch

For a managed Elasticsearch, take a look at [bonsai](https://bonsai.io/)

- Elasticsearch, having been installed with [Homebrew](https://brew.sh), can be started and stopped as a [Homebrew](https://brew.sh) service e.g. for testing:

  ```bash
  brew services start elasticsearch
  ```

  ```bash
  brew services stop elasticsearch
  ```

Elasticsearch can be a tad slow at "warming up". Keep this in mind when testing.

For instance, because the slow start up I ditched the use of [elastic4s](https://sksamuel.github.io/elastic4s) for [elastic-scala-httpclient](https://github.com/bizreach/elastic-scala-httpclient).
Now, even though **elastic4s** should be the bees knees for Scala interacting with Elasticsearch, unfortunately the documentation is not obvious when it comes to "connection timeout" and "retries".
However, **elastic-scala-httpclient** immediately get to the point and allows this to be easily handled in code.

- With the system running (via docker-compose up), test Elasticsearch with a curl
    
  ```bash
  $ curl http://127.0.0.1:9200/_cat/health
  1540670732 20:05:32 docker-cluster green 1 1 0 0 0 0 0 0 - 100.0%
  
  $ curl http://127.0.0.1:9200/_cat/indices
  ```
  
- Create an index

  ```bash
  $ curl -X PUT http://127.0.0.1:9200/twitter
  {
    "acknowledged": true,
    "shards_acknowledged": true,
    "index": "twitter"
  }

  $ curl http://127.0.0.1:9200/_cat/indices
  yellow open twitter b108gZHfSeuCa4uTYuiGkw 5 1 0 0 1.1kb 1.1kb
  ```
  
- Create a document with some JSON
  
  ```bash
  $ curl -0 -v -X PUT http://127.0.0.1:9200/twitter/tweets/1 \
  -H "Content-Type: application/json; charset=utf-8" \
  -d @- << EOF
  
  {
    "course": "Kafka for Beginners",
    "module": "Elasticsearch"
  }
  EOF

  ...
  ...
  {
    "_index": "twitter",
    "_type": "tweets",
    "_id": "1",
    "_version": 1,
    "result": "created",
    "_shards": {
      "total": 2,
      "successful": 1,
      "failed": 0
    },
    "_seq_no": 0,
    "_primary_term": 1
  }
  ```
  
  and get back what we created:
  
  ```bash
  $ curl http://127.0.0.1:9200/twitter/tweets/1
  {
    "_index": "twitter",
    "_type": "tweets",
    "_id": "1",
    "_version": 1,
    "found": true,
    "_source": {
      "course": "Kafka for Beginners",
      "module": "Elasticsearch"
    }
  }
  ```