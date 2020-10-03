package org.net.rss.data

import org.net.rss.Rss
import java.time.ZonedDateTime

data class TimeTrackingRss(val rss: Rss) {
    val lastRefreshedAt = if (rss.items.isNotEmpty()) rss.items.last().pubDate else ZonedDateTime.now()
}
