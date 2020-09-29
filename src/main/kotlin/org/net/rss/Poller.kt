package org.net.rss

import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.io.Fetcher
import java.net.http.HttpClient

class Poller {
    fun poll() {
        val fetcher = Fetcher(HttpClient.newHttpClient())

        Subscriptions.all
          .forEach { InMemoryFeedRepository.add(it.url, fetcher.fetch(it)) }

        val a = 1
    }
}
