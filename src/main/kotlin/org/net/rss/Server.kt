package org.net.rss

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.html.FeedDiv
import java.time.LocalTime
import kotlin.concurrent.thread

fun main() {
    println("${LocalTime.now()}: Retrieving feeds, please wait...")
    Poller().poll()
    println("${LocalTime.now()}: Done.")

    thread {
        while (true) {
            Thread.sleep(FIVE_MINUTES)
            Poller().poll()
        }
    }

    println("Server starting on port 8000.")
    app.asServer(SunHttp(8000)).start()
}

const val FIVE_MINUTES = 5 * 60 * 1000L

val app: HttpHandler = { _ ->
    Response(Status.OK).body(Page(feedDivs()).asHtml())
}

private fun feedDivs(): List<FeedDiv> {
    return Subscriptions
      .all
      .map { InMemoryFeedRepository.get(it.url) }
      .filterNotNull()
      .map { FeedDiv(it) }
}
