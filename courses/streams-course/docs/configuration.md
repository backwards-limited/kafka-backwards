# Configuration

Under the hood a streams application is using the Kafka producer and consumer API and so most of the configuration is the same. However, streams need some extra configuration.

Specific and very important to streams is **application.id**, which is used for:

- Consumer **group.id** = **application.id**
- Default prefix of **client.id**
- Prefix to internal changelog topics

Another configuration that should be set for serialization and deserialization of data is:

- **default.[key | value].serde**