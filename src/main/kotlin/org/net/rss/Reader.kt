package org.net.rss

import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.html.FeedDiv

class Reader {
    fun show(): String {
        val divs = Subscriptions
          .all
          .map { InMemoryFeedRepository.get(it.url) }
          .filterNotNull()
          .map { FeedDiv(it) }
          .map { it.toString() }
          .joinToString(separator = "") { it }

        return "<html><body>${divs}</body></html>"
    }
}
