package org.net.rss

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule

class Subscriptions(yaml: String?) {
    constructor() : this(null)

    private val removeQueries: (String) -> String = { it.substringBefore("?") }

    val all: List<Subscription>

    init {
        if (yaml == null) {
            all = listOf(
              Subscription("https://www.theguardian.com/au/rss"),
              Subscription("https://www.theage.com.au/rss/national/victoria.xml", linkRewrite = removeQueries),
              Subscription("https://www.theage.com.au/rss/national.xml", linkRewrite = removeQueries),
              Subscription("https://www.theage.com.au/rss/culture.xml", linkRewrite = removeQueries),
              Subscription("https://www.theage.com.au/rss/lifestyle.xml", linkRewrite = removeQueries),
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
