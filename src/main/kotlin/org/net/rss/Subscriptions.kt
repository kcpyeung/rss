package org.net.rss

import java.time.format.DateTimeFormatter

object Subscriptions {
    private val removeQueries: (String) -> String = { it.substringBefore("?") }

    val all = listOf(
      Subscription("https://www.theguardian.com/au/rss", DateTimeFormatter.RFC_1123_DATE_TIME),
      Subscription("https://www.theage.com.au/rss/national/victoria.xml", DateTimeFormatter.RFC_1123_DATE_TIME, removeQueries),
      Subscription("https://www.theage.com.au/rss/national.xml", DateTimeFormatter.RFC_1123_DATE_TIME, removeQueries),
      Subscription("https://www.theage.com.au/rss/culture.xml", DateTimeFormatter.RFC_1123_DATE_TIME, removeQueries),
      Subscription("https://www.theage.com.au/rss/lifestyle.xml", DateTimeFormatter.RFC_1123_DATE_TIME, removeQueries),
    )
}
