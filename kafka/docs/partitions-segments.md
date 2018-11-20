# Partitions and Segments

- Topics are made of partitions

- Partitions are made of segments (which are essentially files)

- Only one segment is **active*** (the one data is being written to) and there are 2 settings:
  - log.segment.bytes - maximum size of a single segment in bytes
  - log.segment.ms - time Kafka will wait until committing (closing) the segment if not full

> ![Segments](docs/images/segments.png)