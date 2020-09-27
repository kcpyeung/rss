package org.net.rss.data

import org.net.rss.Rss

class InMemoryFeedRepository {
    val feeds = hashMapOf<String, Rss>()

    fun add(source: String, rss: Rss) {
        val stored = feeds[source]
        if (stored == null) {
            feeds[source] = rss
        } else {
            stored.items.addAll(rss.items)
        }
    }

    fun hasSource(source: String): Boolean {
        return feeds.containsKey(source)
    }

    fun get(source: String): Rss? {
        return feeds[source]
    }
}
