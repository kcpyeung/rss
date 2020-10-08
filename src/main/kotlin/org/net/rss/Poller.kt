package org.net.rss

import org.net.rss.config.Subscription
import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.io.Fetcher
import java.net.http.HttpClient
import java.time.LocalTime
import java.util.*

class Poller(subscriptions: List<Subscription>) {
    val poll = {
        println("${LocalTime.now()}: START polling")

        val http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()
        val fetcher = Fetcher(http)

        subscriptions
          .parallelStream()
          .map { fetcher.fetch(it) }
          .filter(Objects::nonNull)
          .forEach { InMemoryFeedRepository.add(it!!) }

        println("${LocalTime.now()}: DONE polling")
    }
}
