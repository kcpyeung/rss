package org.net.rss.data

import org.net.rss.Rss

class InMemoryFeedRepository {
    val feeds = hashMapOf<String, Rss>()

    fun add(source: String, rss: Rss) {
        feeds[source] = rss
    }

    fun hasSource(source: String): Boolean {
        return feeds.containsKey(source)
    }

    fun get(source: String): Rss? {
        return feeds[source]
    }
}
