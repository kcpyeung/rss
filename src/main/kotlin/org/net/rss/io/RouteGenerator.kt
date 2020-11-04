package org.net.rss.io

import org.http4k.core.Method.POST
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.net.rss.Poller
import org.net.rss.config.Section
import org.net.rss.config.Subscription
import org.net.rss.config.Subscriptions
import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.html.FeedDiv
import org.net.rss.html.HomePage
import org.net.rss.html.SectionPage

class RouteGenerator(subscriptions: Subscriptions) {
    val routes: RoutingHttpHandler

    init {
        val mappings = subscriptions
          .sections
          .map { section -> "/${section.name}" bind GET to { sectionPageOrRoot(section) } }
          .toMutableList()

        mappings.add("/" bind GET to { Response(OK).body(HomePage(subscriptions).asHtml()) })
        mappings.add("/read/{feed:.*}/{item:.*}" bind GET to { request -> markRead(request) })
        mappings.add("/fetch" bind POST to { fetch(subscriptions) })

        routes = routes(*mappings.toTypedArray())
    }

    private fun fetch(subscriptions: Subscriptions): Response {
        Poller(subscriptions.all).poll()
        return Response(SEE_OTHER).header("Location", "/")
    }

    private fun sectionPageOrRoot(section: Section) =
      if (section.hasUnreadItems()) {
          Response(OK).body(SectionPage(feedDivs(section.subscriptions)).asHtml())
      } else {
          Response(SEE_OTHER).header("Location", "/")
      }

    private fun feedDivs(subs: List<Subscription>): List<FeedDiv> {
        return subs
          .mapNotNull { InMemoryFeedRepository.get(it.feedIdGen(it.url)) }
          .map { FeedDiv(it) }
    }

    fun markRead(request: Request): Response {
        val feed = request.path("feed")
        val item = request.path("item")

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
