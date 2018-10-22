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

- With the system running (via docker-compose up), test Elasticsearch with a curl:
    
  ```bash
  curl http://0.0.0.0:9200/twitter/_search?q=user:kimchy
  ```