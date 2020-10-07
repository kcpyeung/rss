package org.net.rss.data

import org.net.rss.Feed
import java.util.*

object InMemoryFeedRepository {
    private val feeds = Collections.synchronizedMap(hashMapOf<String, TimeTrackingRss>())

    fun add(feed: Feed) {
        val stored = feeds[feed.id]
        if (stored == null) {
            feeds[feed.id] = TimeTrackingRss(feed)
        } else {
            val storedRss = stored.feed
            storedRss.addItems(feed.items, stored.lastRefreshedAt)
            feeds[feed.id] = TimeTrackingRss(storedRss)
        }
    }

    fun hasSource(source: String): Boolean {
        return feeds.containsKey(source)
    }

    fun get(source: String): Feed? {
        return feeds[source]?.feed
    }

    internal fun clear() {
        feeds.clear()
    }

    fun deleteTo(source: String, guid: String) {
        val rss = feeds[source]?.feed ?: return
        rss.deleteTo(guid)
        feeds[source] = TimeTrackingRss(rss)
    }
}
