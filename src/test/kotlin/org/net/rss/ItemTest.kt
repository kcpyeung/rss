package org.net.rss

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ItemTest {
    val subscription = Subscription("https://www.theguardian.com/au/rss", DateTimeFormatter.RFC_1123_DATE_TIME)

    val rss = """
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
  </channel>
</rss>
""".trimIndent()

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
        <guid>F44F0AAA-78ED-4374-9F93-AFF05829E218</guid>
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
    fun item_has_basic_properties() {
        val item = Rss(rss, subscription).items[0]

        assertThat(item.description, `is`("<p>Indigenous Australians are particularly vulnerable to coronavirus, but Aboriginal health workers in Melbourne are using their intimate knowledge of their community to ensure critical health messages get through.</p>"))
        assertThat(item.link, `is`("https://www.abc.net.au/news/2020-09-15/indigenous-communities-in-melbourne-spread-coronavirus-message/12662598"))
        assertThat(item.title, `is`("How a 'heaven-sent' health worker is helping keep coronavirus cases low in the Indigenous community"))
    }

    @Test
    fun all_items_available() {
        val rss = Rss(rssWith2Items, subscription)

        assertThat(rss.items[0].description, `is`("Footage of the man's arrest showed him running from police and striking a police car, before he was hit by a police car."))
        assertThat(rss.items[0].link, `is`("https://www.abc.net.au/news/2020-09-14/footage-shows-the-man-being-struck-by-a-police-car./12663386"))
        assertThat(rss.items[0].title, `is`("Footage of a man being arrested by police in Melbourne's north."))

        assertThat(rss.items[1].description, `is`("<p>Indigenous Australians are particularly vulnerable to coronavirus, but Aboriginal health workers in Melbourne are using their intimate knowledge of their community to ensure critical health messages get through.</p>"))
        assertThat(rss.items[1].link, `is`("https://www.abc.net.au/news/2020-09-15/indigenous-communities-in-melbourne-spread-coronavirus-message/12662598"))
        assertThat(rss.items[1].title, `is`("How a 'heaven-sent' health worker is helping keep coronavirus cases low in the Indigenous community"))
    }

    @Test
    fun `pubDate defaults to current time if missing from rss xml`() {
        val clock = Clock.fixed(Instant.parse("2020-09-27T07:30:00Z"), ZoneId.of("+10:00"))
        mockkStatic("java.time.ZonedDateTime")
        every { ZonedDateTime.now() } returns ZonedDateTime.now(clock)

        val rss = Rss(rssWith2Items, subscription)

        assertThat(rss.items[0].pubDate, `is`(ZonedDateTime.ofInstant(Instant.parse("2020-09-14T10:51:10Z"), ZoneId.of("+10:00"))))
        assertThat(rss.items[1].pubDate, `is`(ZonedDateTime.ofInstant(Instant.parse("2020-09-27T07:30:00Z"), ZoneId.of("+10:00"))))
    }

    @AfterEach
    fun unmock() = unmockkAll()

    @Test
    fun `supplied guid is ignored and item guid is calculated`() {
        val rss = Rss(rssWith2Items, subscription)

        assertThat(rss.items[0].guid, `is`("S703KZLSnJyg9pgrpB8toGqhEQg="))
        assertThat(rss.items[1].guid, `is`("qdmNgHhyl9deZuRho6wd8lssaDQ="))
    }

    @Nested
    inner class Sorting {
        private val sub0 = Subscription("https://some.site", DateTimeFormatter.RFC_1123_DATE_TIME, idGenerator = { _ -> "00000"})
        private val sub1 = Subscription("https://some.site", DateTimeFormatter.RFC_1123_DATE_TIME, idGenerator = { _ -> "11111"})

        @Test
        fun `sort items by pubDate ascendingly if different`() {
            val i1 = Item({ if (it == "pubDate") "Tue, 15 Sep 2020 20:51:10 +1000" else null }, sub1)
            val i2 = Item({ if (it == "pubDate") "Mon, 14 Sep 2020 20:51:10 +1000" else null }, sub0)

            val list = listOf(i1, i2)
            val sorted = list.sorted()

            assertThat(sorted.get(0).guid, `is`("00000"))
            assertThat(sorted.get(1).guid, `is`("11111"))
        }

        @Test
        fun `sort items by guid ascendingly if pubDate identical`() {
            val i1 = Item({ if (it == "pubDate") "Tue, 15 Sep 2020 20:51:10 +1000" else null }, sub0)
            val i2 = Item({ if (it == "pubDate") "Tue, 15 Sep 2020 20:51:10 +1000" else null }, sub1)

            val list = listOf(i1, i2)
            val sorted = list.sorted()

            assertThat(sorted.get(0).guid, `is`("00000"))
            assertThat(sorted.get(1).guid, `is`("11111"))
        }
    }

    @Nested
    inner class LinkRewrite {
        @Test
        fun `if not specified, no link rewrite happens`() {
            val item = Item({ if (it == "link") "https://some.com/real_news.html" else null }, subscription)

            assertThat(item.link, `is`("https://some.com/real_news.html"))
        }

        @Test
        fun `if specified, link rewrite lambda is invoked to rewrite the link`() {
            val subscription = Subscription("https://some.com/real_news.html", DateTimeFormatter.RFC_1123_DATE_TIME, { it.replace("real", "FAKE") })
            val item = Item({ if (it == "link") "https://some.com/real_news.html" else null }, subscription)

            assertThat(item.link, `is`("https://some.com/FAKE_news.html"))
        }
    }
}
