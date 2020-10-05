package org.net.rss

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.format.DateTimeFormatter

class Subscriptions(yaml: String?) {
    constructor() : this(null)

    private val removeQueries: (String) -> String = { it.substringBefore("?") }

    val all: List<Subscription>

    init {
        if (yaml == null) {
            all = listOf(
              Subscription("https://www.theguardian.com/au/rss", DateTimeFormatter.RFC_1123_DATE_TIME),
              Subscription("https://www.theage.com.au/rss/national/victoria.xml", DateTimeFormatter.RFC_1123_DATE_TIME, removeQueries),
              Subscription("https://www.theage.com.au/rss/national.xml", DateTimeFormatter.RFC_1123_DATE_TIME, removeQueries),
              Subscription("https://www.theage.com.au/rss/culture.xml", DateTimeFormatter.RFC_1123_DATE_TIME, removeQueries),
              Subscription("https://www.theage.com.au/rss/lifestyle.xml", DateTimeFormatter.RFC_1123_DATE_TIME, removeQueries),
            )
        } else {
            val objectMapper = ObjectMapper(YAMLFactory())
            objectMapper.registerModule(KotlinModule())

            all = objectMapper
              .readValue(yaml, Array<Subscription>::class.java)
              .toList()
        }
    }
}
