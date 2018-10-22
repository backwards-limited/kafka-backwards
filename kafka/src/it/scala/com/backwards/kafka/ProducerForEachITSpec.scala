package com.backwards.kafka

import org.scalatest.OneInstancePerTest
import com.backwards.container._

class ProducerForEachITSpec extends ProducerITSpec with ContainerFixture with ForEachContainerLifecycle with OneInstancePerTest