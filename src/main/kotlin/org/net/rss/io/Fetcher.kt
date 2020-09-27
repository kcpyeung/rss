package org.net.rss.io

import org.net.rss.Rss
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class Fetcher(private val http: HttpClient) {
    fun fetch(url: String): Rss {
        val request = toRequest(url)

        val xml = http.send(request, HttpResponse.BodyHandlers.ofString()).body()
        return Rss(xml)
    }

    private fun toRequest(url: String): HttpRequest {
        return HttpRequest
          .newBuilder(URI.create(url))
          .build()
    }
}
