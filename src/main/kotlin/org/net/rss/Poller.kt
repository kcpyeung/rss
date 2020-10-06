package org.net.rss

import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.io.Fetcher
import java.net.http.HttpClient
import java.time.LocalTime

class Poller(subscriptions: List<Subscription>) {
    val poll = {
        println("${LocalTime.now()}: START polling")

        val fetcher = Fetcher(HttpClient.newHttpClient())

        subscriptions
          .parallelStream()
          .map { fetcher.fetch(it) }
          .forEach { InMemoryFeedRepository.add(it) }

        println("${LocalTime.now()}: DONE polling")
    }
}
