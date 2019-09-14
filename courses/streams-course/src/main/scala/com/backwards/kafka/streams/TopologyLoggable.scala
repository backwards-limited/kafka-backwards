package com.backwards.kafka.streams

import scribe.Loggable
import scribe.output.{Color, ColoredOutput, TextOutput}
import org.apache.kafka.streams.Topology

object TopologyLoggable {
  implicit val topologyLoggable: Loggable[Topology] =
    (value: Topology) => new ColoredOutput(Color.Yellow, new TextOutput(value.describe().toString))
}