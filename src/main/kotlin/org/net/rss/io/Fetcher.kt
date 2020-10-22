package org.net.rss.io

import org.net.rss.Atom
import org.net.rss.Feed
import org.net.rss.Rss
import org.net.rss.config.Subscription
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class Fetcher(http: HttpClient) {
    val fetch: (Subscription) -> Feed? = { subscription ->
        try {
            val request = toRequest(subscription.url)

            val xml = http.send(request, HttpResponse.BodyHandlers.ofString()).body()
            toFeed(xml, subscription)
        } catch (t: Throwable) {
            println("error fetching ${subscription.url}")
            println(t.message)
            println()
            null
        }
    }

    private fun toFeed(xml: String, subscription: Subscription): Feed {
        if (xml.contains("</feed>")) {
            return Atom(xml, subscription)
        } else {
            return Rss(xml, subscription)
        }
    }

    private fun toRequest(url: String): HttpRequest {
        return HttpRequest
          .newBuilder(URI.create(url))
          .build()
    }
}
