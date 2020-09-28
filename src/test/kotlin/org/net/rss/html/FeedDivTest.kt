package org.net.rss.html

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.net.rss.Rss
import java.time.format.DateTimeFormatter

class FeedDivTest {
    val dateFormat = DateTimeFormatter.RFC_1123_DATE_TIME

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

    @Nested inner class FeedRelatedTests {
        @Test
        fun div_id_is_feed_title() {
            val div = FeedDiv(Rss(rssWith2Items, dateFormat))

            assertThat(div.id, `is`("victoria_articles_feed"))
        }

        @Test
        fun feed_div_has_title_plus_items() {
            val div = FeedDiv(Rss(rssWith2Items, dateFormat))

            assertThat(div.children.size, `is`(2))
        }

        @Test
        fun feed_div_contains_feed_title() {
            val div = FeedDiv(Rss(rssWith2Items, dateFormat))

            val toString = div.toString()

            assertThat(toString, startsWith("""<div id="victoria_articles_feed">Victoria articles feed"""))
            assertThat(toString, endsWith("</div>"))
        }

        @Test
        fun `full feed html`() {
            val div = FeedDiv(Rss(rssWith2Items, dateFormat))

            assertThat(div.toString(), `is`("""
                <div id="victoria_articles_feed">Victoria articles feed
                <div id="victoria_articles_feed-0">
                    <div><a href="https://www.abc.net.au/news/2020-09-14/footage-shows-the-man-being-struck-by-a-police-car./12663386">Footage of a man being arrested by police in Melbourne's north.</a></div>
                    <div>Footage of the man's arrest showed him running from police and striking a police car, before he was hit by a police car.</div>
                </div>
                <div id="victoria_articles_feed-1">
                    <div><a href="https://www.abc.net.au/news/2020-09-15/indigenous-communities-in-melbourne-spread-coronavirus-message/12662598">How a 'heaven-sent' health worker is helping keep coronavirus cases low in the Indigenous community</a></div>
                    <div><p>Indigenous Australians are particularly vulnerable to coronavirus, but Aboriginal health workers in Melbourne are using their intimate knowledge of their community to ensure critical health messages get through.</p></div>
                </div>
                </div>
            """.trimIndent()))
        }
    }
}
