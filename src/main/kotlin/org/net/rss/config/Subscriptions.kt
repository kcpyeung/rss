package org.net.rss.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule

class Subscriptions(yaml: String?) {
    constructor() : this(null)

    val all: List<Subscription>

    init {
        if (yaml == null) {
            all = listOf(
              Subscription("https://www.theage.com.au/rss/culture.xml"),
            )
        } else {
            val objectMapper = ObjectMapper(YAMLFactory())
            objectMapper.registerModule(KotlinModule())

            all = objectMapper
              .readValue(yaml, Array<SubscriptionDto>::class.java)
              .toList()
              .map { Subscription(it) }
        }
    }
}
