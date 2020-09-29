package org.net.rss

import java.time.format.DateTimeFormatter

object Subscriptions {
    val all = arrayOf(
      Subscription("https://www.theguardian.com/au/rss", DateTimeFormatter.RFC_1123_DATE_TIME),
      Subscription("https://www.theage.com.au/rss/national/victoria.xml", DateTimeFormatter.RFC_1123_DATE_TIME),
    )
}
