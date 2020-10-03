package org.net.rss.data

import org.net.rss.Rss
import java.util.*

object InMemoryFeedRepository {
    private val feeds = Collections.synchronizedMap(hashMapOf<String, TimeTrackingRss>())

    fun add(rss: Rss) {
        val stored = feeds[rss.id]
        if (stored == null) {
            feeds[rss.id] = TimeTrackingRss(rss)
        } else {
            val storedRss = stored.rss
            storedRss.addItems(rss.items, stored.lastRefreshedAt)
            feeds[rss.id] = TimeTrackingRss(storedRss)
        }
    }

    fun hasSource(source: String): Boolean {
        return feeds.containsKey(source)
    }

    fun get(source: String): Rss? {
        return feeds[source]?.rss
    }

    internal fun clear() {
        feeds.clear()
    }

    fun deleteTo(source: String, guid: String) {
        val rss = feeds[source]?.rss ?: return
        rss.deleteTo(guid)
        feeds[source] = TimeTrackingRss(rss)
    }
}
