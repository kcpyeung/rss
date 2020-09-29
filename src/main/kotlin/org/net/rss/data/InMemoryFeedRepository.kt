package org.net.rss.data

import org.net.rss.Rss

object InMemoryFeedRepository {
    private val feeds = hashMapOf<String, Rss>()

    fun add(source: String, rss: Rss) {
        val stored = feeds[source]
        if (stored == null) {
            feeds[source] = rss
        } else {
            stored.addItems(rss.items)
        }
    }

    fun hasSource(source: String): Boolean {
        return feeds.containsKey(source)
    }

    fun get(source: String): Rss? {
        return feeds[source]
    }

    internal fun clear() {
        feeds.clear()
    }
}
