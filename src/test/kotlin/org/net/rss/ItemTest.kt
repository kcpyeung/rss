package org.net.rss

import io.mockk.every
import io.mockk.mockkStatic
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ItemTest {

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
        val item = Rss(rss).items[0]

        assertThat(item.description, `is`("<p>Indigenous Australians are particularly vulnerable to coronavirus, but Aboriginal health workers in Melbourne are using their intimate knowledge of their community to ensure critical health messages get through.</p>"))
        assertThat(item.link, `is`("https://www.abc.net.au/news/2020-09-15/indigenous-communities-in-melbourne-spread-coronavirus-message/12662598"))
        assertThat(item.title, `is`("How a 'heaven-sent' health worker is helping keep coronavirus cases low in the Indigenous community"))
    }

    @Test
    fun all_items_available() {
        val rss = Rss(rssWith2Items)

        assertThat(rss.items[0].description, `is`("<p>Indigenous Australians are particularly vulnerable to coronavirus, but Aboriginal health workers in Melbourne are using their intimate knowledge of their community to ensure critical health messages get through.</p>"))
        assertThat(rss.items[0].link, `is`("https://www.abc.net.au/news/2020-09-15/indigenous-communities-in-melbourne-spread-coronavirus-message/12662598"))
        assertThat(rss.items[0].title, `is`("How a 'heaven-sent' health worker is helping keep coronavirus cases low in the Indigenous community"))

        assertThat(rss.items[1].description, `is`("Footage of the man's arrest showed him running from police and striking a police car, before he was hit by a police car."))
        assertThat(rss.items[1].link, `is`("https://www.abc.net.au/news/2020-09-14/footage-shows-the-man-being-struck-by-a-police-car./12663386"))
        assertThat(rss.items[1].title, `is`("Footage of a man being arrested by police in Melbourne's north."))
    }

    @Test
    fun `pubDate defaults to current time if missing from rss xml`() {
        val clock = Clock.fixed(Instant.parse("2020-09-27T07:30:00Z"), ZoneId.of("Australia/Melbourne"))
        mockkStatic("java.time.ZonedDateTime")
        every { ZonedDateTime.now() } returns ZonedDateTime.now(clock)

        val rss = Rss(rssWith2Items)

        assertThat(rss.items[0].pubDate, `is`("Sun, 27 Sep 2020 17:30:00 +1000"))
        assertThat(rss.items[1].pubDate, `is`("Mon, 14 Sep 2020 20:51:10 +1000"))
    }
}
