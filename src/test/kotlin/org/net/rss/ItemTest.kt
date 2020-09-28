package org.net.rss

import io.mockk.every
import io.mockk.mockkStatic
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

class ItemTest {
    val dateFormat = DateTimeFormatter.RFC_1123_DATE_TIME

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
        val item = Rss(rss, dateFormat).items[0]

        assertThat(item.description, `is`("<p>Indigenous Australians are particularly vulnerable to coronavirus, but Aboriginal health workers in Melbourne are using their intimate knowledge of their community to ensure critical health messages get through.</p>"))
        assertThat(item.link, `is`("https://www.abc.net.au/news/2020-09-15/indigenous-communities-in-melbourne-spread-coronavirus-message/12662598"))
        assertThat(item.title, `is`("How a 'heaven-sent' health worker is helping keep coronavirus cases low in the Indigenous community"))
    }

    @Test
    fun all_items_available() {
        val rss = Rss(rssWith2Items, dateFormat)

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

        val rss = Rss(rssWith2Items, dateFormat)

        assertThat(rss.items[0].pubDate, `is`(ZonedDateTime.ofInstant(Instant.parse("2020-09-14T10:51:10Z"), ZoneId.of("+10:00"))))
        assertThat(rss.items[1].pubDate, `is`(ZonedDateTime.ofInstant(Instant.parse("2020-09-27T07:30:00Z"), ZoneId.of("+10:00"))))
    }

    @Test
    fun `generates item guid if missing from rss xml`() {
        mockkStatic("java.util.Base64")
        every {
            Base64.getEncoder().encodeToString(any())
        } returns "kICr1LONJhcb6BKHyiM7BIzCFrU="

        val rss = Rss(rssWith2Items, dateFormat)

        assertThat(rss.items[0].guid, `is`("F44F0AAA-78ED-4374-9F93-AFF05829E218"))
        assertThat(rss.items[1].guid, `is`("kICr1LONJhcb6BKHyiM7BIzCFrU="))
    }

    @Nested
    inner class Sorting {
        private fun get(xml: String): NodeList {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val xmlInput = InputSource(StringReader(xml))
            val doc = dBuilder.parse(xmlInput)

            doc.normalizeDocument()

            return doc.getElementsByTagName("item")
        }

        @Test
        fun `sort items by pubDate ascendingly if different`() {
            val i1 = Item(get("""<item>
                <guid>later</guid><title>t1</title><link>https://news.org/1</link><description>d1</description>
                <pubDate>Tue, 15 Sep 2020 20:51:10 +1000</pubDate>
                </item>""".trimIndent()).item(0), DateTimeFormatter.RFC_1123_DATE_TIME)
            val i2 = Item(get("""<item>
                <guid>earlier</guid><title>t2</title><link>https://news.org/2</link><description>d2</description>
                <pubDate>Mon, 14 Sep 2020 20:51:10 +1000</pubDate>
                </item>""".trimIndent()).item(0), DateTimeFormatter.RFC_1123_DATE_TIME)

            val list = listOf(i1, i2)
            val sorted = list.sorted()

            assertThat(sorted.get(0).guid, `is`("earlier"))
            assertThat(sorted.get(1).guid, `is`("later"))
        }

        @Test
        fun `sort items by guid ascendingly if pubDate identical`() {
            val i1 = Item(get("""<item>
                <guid>11111</guid><title>t1</title><link>https://news.org/1</link><description>d1</description>
                <pubDate>Tue, 15 Sep 2020 20:51:10 +1000</pubDate>
                </item>""".trimIndent()).item(0), DateTimeFormatter.RFC_1123_DATE_TIME)
            val i2 = Item(get("""<item>
                <guid>00000</guid><title>t2</title><link>https://news.org/2</link><description>d2</description>
                <pubDate>Tue, 15 Sep 2020 20:51:10 +1000</pubDate>
                </item>""".trimIndent()).item(0), DateTimeFormatter.RFC_1123_DATE_TIME)

            val list = listOf(i1, i2)
            val sorted = list.sorted()

            assertThat(sorted.get(0).guid, `is`("00000"))
            assertThat(sorted.get(1).guid, `is`("11111"))
        }
    }
}
