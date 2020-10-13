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
import org.net.rss.config.Subscriptions
import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.html.FeedDiv
import org.net.rss.html.Page
import java.io.File
import kotlin.concurrent.thread

private val subscriptions = Subscriptions(readConfig("mine.yaml"))

private val poller = Poller(subscriptions.all)

fun main(args: Array<String>) {
    poller.poll()

    thread {
        while (true) {
            Thread.sleep(FIVE_MINUTES)
            poller.poll()
        }
    }

    val port = port(args)
    println("Server starting on port ${port}.")
    app.asServer(SunHttp(port)).start()
}

const val FIVE_MINUTES = 5 * 60 * 1000L

val app = routes(
  "/" bind GET to { _ -> Response(OK).body(Page(feedDivs()).asHtml()) },
  "/read/{feed:.*}/{item:.*}" bind GET to { request -> markRead(request.path("feed"), request.path("item")) },
)

private fun feedDivs(): List<FeedDiv> {
    return subscriptions
      .all
      .mapNotNull { InMemoryFeedRepository.get(it.feedIdGen(it.url)) }
      .map { FeedDiv(it) }
}

private fun port(args: Array<String>): Int {
    if (args.isNotEmpty()) {
        return args[0].toInt()
    } else {
        return 8000
    }
}

fun markRead(feed: String?, item: String?): Response {
    if (feed != null && item != null) {
        InMemoryFeedRepository.deleteTo(feed, item)
    }
    return Response(SEE_OTHER).header("Location", "/")
}

fun readConfig(filename: String): String {
    return File(filename).readText()
}
