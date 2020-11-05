package org.net.rss

import org.net.rss.config.Subscription
import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.io.Fetcher
import java.net.http.HttpClient
import java.time.LocalTime
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Poller(subscriptions: List<Subscription>) {
    val poll = {
        println("${LocalTime.now()}: START polling")

        val threads = Executors.newCachedThreadPool()

        subscriptions
          .parallelStream()
          .map { fetcher(threads).fetch(it) }
          .filter(Objects::nonNull)
          .forEach { InMemoryFeedRepository.add(it!!) }

        threads.shutdownNow()

        println("${LocalTime.now()}: DONE polling")
    }

    private fun fetcher(threads: ExecutorService): Fetcher {
        val http = HttpClient
          .newBuilder()
          .executor(threads)
          .followRedirects(HttpClient.Redirect.ALWAYS)
          .build()
        return Fetcher(http)
    }
}
