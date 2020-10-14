package org.net.rss.io

import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.net.rss.config.Subscription
import org.net.rss.config.Subscriptions
import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.html.FeedDiv
import org.net.rss.html.Page

class RouteGenerator(subscriptions: Subscriptions) {
    val routes: RoutingHttpHandler

    init {
        val mappings = subscriptions
          .sections
          .map { section -> "/${section.name}" bind GET to { Response(OK).body(Page(feedDivs(section.subscriptions)).asHtml()) } }
          .toMutableList()

        mappings.add("/" bind GET to { Response(OK) })
        mappings.add("/read/{feed:.*}/{item:.*}" bind GET to { request -> markRead(request) })

        routes = routes(*mappings.toTypedArray())
    }

    private fun feedDivs(subs: List<Subscription>): List<FeedDiv> {
        return subs
          .mapNotNull { InMemoryFeedRepository.get(it.feedIdGen(it.url)) }
          .map { FeedDiv(it) }
    }

    fun markRead(request: Request): Response {
        val feed = request.path("feed")
        val item = request.path("item")

        println(request)

        if (feed != null && item != null) {
            InMemoryFeedRepository.deleteTo(feed, item)
        }
        return Response(SEE_OTHER).header("Location", whereFrom(request))
    }

    private fun whereFrom(request: Request): String {
        val referer = request.header("Referer")
        return referer ?: "/"
    }
}
