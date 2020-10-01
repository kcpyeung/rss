package org.net.rss

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.SunHttp
import org.http4k.server.asServer

fun main() {
    println("Retrieving feeds, please wait...")
    Poller().poll()

    println("Server starting on port 8000.")
    app.asServer(SunHttp(8000)).start()
}

val app: HttpHandler = { _ -> Response(Status.OK).body(Reader().show()) }
