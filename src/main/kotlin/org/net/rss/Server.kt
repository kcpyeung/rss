package org.net.rss

import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
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

val app = routes(
  "/" bind GET to { _ -> Response(OK).body(Page(feedDivs()).asHtml()) },
  "/read/{feed:.*}/{item:.*}" bind GET to { request -> markRead(request.path("feed"), request.path("item")) },
)

private fun feedDivs(): List<FeedDiv> {
    return Subscriptions
      .all
      .mapNotNull { InMemoryFeedRepository.get(it.feedIdGen(it.url)) }
      .map { FeedDiv(it) }
}

fun markRead(feed: String?, item: String?): Response {
    if (feed != null && item != null) {
        InMemoryFeedRepository.deleteTo(feed, item)
    }
    return Response(SEE_OTHER).header("Location", "/")
}
