package org.net.rss.io

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.net.rss.config.Subscription
import java.net.ConnectException
import java.net.http.HttpClient
import java.net.http.HttpResponse
import java.time.format.DateTimeFormatter

class FetcherTest {
    val rssWith2Items = """
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/"
     xmlns:media="http://search.yahoo.com/mrss/">
  <channel>
    <title>Victoria articles feed</title>
    <category>Australian Broadcasting Corporation: All content</category>
    <item>
        <title>How a &#039;heaven-sent&#039; health worker is helping keep coronavirus cases low in the Indigenous community</title>
        <link>https://www.abc.net.au/news/2020-09-15/indigenous-communities-in-melbourne-spread-coronavirus-message/12662598</link>
        <description>
              <![CDATA[
              <p>Indigenous Australians are particularly vulnerable to coronavirus, but Aboriginal health workers in Melbourne are using their intimate knowledge of their community to ensure critical health messages get through.</p>
              ]]>
        </description>
    </item>
    <item>
        <title>Footage of a man being arrested by police in Melbourne&#039;s north.</title>
        <link>https://www.abc.net.au/news/2020-09-14/footage-shows-the-man-being-struck-by-a-police-car./12663386</link>
        <description>
              <![CDATA[
              Footage of the man's arrest showed him running from police and striking a police car, before he was hit by a police car.
              ]]>
        </description>
        <pubDate>Mon, 14 Sep 2020 20:51:10 +1000</pubDate>
    </item>
  </channel>
</rss>
""".trimIndent()

    @Test
    fun `fetches from website and returns feed`() {
        val http = mockk<HttpClient>()
        val response = mockk<HttpResponse<String>>()
        every { http.send(any(), HttpResponse.BodyHandlers.ofString()) } returns response
        every { response.body() } returns rssWith2Items

        val fetcher = Fetcher(http)
        val feed = fetcher.fetch(Subscription("https://realnews.net/rss", DateTimeFormatter.RFC_1123_DATE_TIME))

        assertThat(feed?.title, `is`("Victoria articles feed"))
        assertThat(feed?.items?.size, `is`(2))
    }

    @Test
    fun `fetch errors return null`() {
        val http = mockk<HttpClient>()
        every { http.send(any(), HttpResponse.BodyHandlers.ofString()) } throws ConnectException("Unknown host")

        val fetcher = Fetcher(http)
        val feed = fetcher.fetch(Subscription("https://realnews.net/rss", DateTimeFormatter.RFC_1123_DATE_TIME))

        assertThat(feed, `is`(nullValue()))
    }
}
