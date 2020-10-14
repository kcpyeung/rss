package org.net.rss.io

import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.net.rss.config.Subscriptions

class RouteGenerator(subscriptions: Subscriptions) {
    val routes: RoutingHttpHandler

    init {
        val mappings = subscriptions
          .sections
          .map { "/${it.name}" bind GET to { Response(OK) } }
          .toMutableList()

        mappings.add("/" bind GET to { Response(OK) })
        mappings.add("/read/{feed:.*}/{item:.*}" bind GET to { Response(OK) })

        routes = routes(*mappings.toTypedArray())
    }
}
