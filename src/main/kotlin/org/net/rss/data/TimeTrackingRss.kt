package org.net.rss.data

import org.net.rss.Feed
import java.time.ZonedDateTime

data class TimeTrackingRss(val feed: Feed) {
    val lastRefreshedAt = if (feed.items.isNotEmpty()) feed.items.last().pubDate else ZonedDateTime.now()
}
