package org.net.rss

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.html.FeedDiv
import java.time.LocalTime

fun main() {
    println("${LocalTime.now()}: Retrieving feeds, please wait...")
    Poller().poll()
    println("${LocalTime.now()}: Done.")

    println("Server starting on port 8000.")
    app.asServer(SunHttp(8000)).start()
}

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
