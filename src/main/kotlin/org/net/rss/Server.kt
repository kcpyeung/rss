package org.net.rss

import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.net.rss.config.Subscriptions
import org.net.rss.io.RouteGenerator
import java.io.File
import kotlin.concurrent.thread

private val subscriptions = Subscriptions(readConfig("mine.yaml"))

private val app = RouteGenerator(subscriptions).routes

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

private fun port(args: Array<String>): Int {
    if (args.isNotEmpty()) {
        return args[0].toInt()
    } else {
        return 8000
    }
}

fun readConfig(filename: String): String {
    return File(filename).readText()
}
