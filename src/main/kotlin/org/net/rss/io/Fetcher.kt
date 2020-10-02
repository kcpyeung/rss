package org.net.rss.io

import org.net.rss.Rss
import org.net.rss.Subscription
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class Fetcher(private val http: HttpClient) {
    fun fetch(subscription: Subscription): Rss {
        val request = toRequest(subscription.url)

        val xml = http.send(request, HttpResponse.BodyHandlers.ofString()).body()
        return Rss(xml, subscription)
    }

    private fun toRequest(url: String): HttpRequest {
        return HttpRequest
          .newBuilder(URI.create(url))
          .build()
    }
}
