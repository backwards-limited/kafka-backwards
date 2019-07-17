package com.backwards.streaming.spark

import java.nio.file.Path

final case class SparkConfig(execution: Execution, data: Data, output: Output)

final case class Execution(mode: String)

final case class Data(host: String, port: Int)

final case class Output(dir: Path)