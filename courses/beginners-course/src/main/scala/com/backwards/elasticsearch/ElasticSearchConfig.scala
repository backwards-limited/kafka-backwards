package com.backwards.elasticsearch

import com.backwards.config.{BootstrapConfig, CredentialsConfig}

final case class ElasticSearchConfig(bootstrap: BootstrapConfig, credentials: CredentialsConfig)