# Avro

The data that you send over Kafka evolves e.g. the following example was sending messages, but the schema has been updated to include **phone number**:

![Schema evolution](images/schema-evolution.png)

A **Schema Registry** is one solution to handling different message versions and Avro works well with this:

![Schema registry](images/schema-registry.png)