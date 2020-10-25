package com.backwards.kafka

import org.scalatest.OneInstancePerTest
import com.backwards.container._

class ProducerForEachSpec extends ProducerSpec with ContainerFixture with ForEachContainerLifecycle with OneInstancePerTest