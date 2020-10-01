package org.net.rss

import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.io.Fetcher
import java.net.http.HttpClient

class Poller {
    fun poll() {
        val fetcher = Fetcher(HttpClient.newHttpClient())

        Subscriptions.all
          .parallelStream()
          .map { Pair(it.url, fetcher.fetch(it)) }
          .forEach { InMemoryFeedRepository.add(it.first, it.second) }
    }
}
