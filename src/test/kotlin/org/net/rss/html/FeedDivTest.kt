package org.net.rss.html

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.net.rss.Rss
import org.net.rss.config.Subscription
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FeedDivTest {
    val subscription = Subscription("https://www.theguardian.com/au/rss", DateTimeFormatter.RFC_1123_DATE_TIME)

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
    fun div_id_is_feed_title() {
        val div = FeedDiv(Rss(rssWith2Items, subscription))

        assertThat(div.id, `is`("victoria_articles_feed"))
    }

    @Test
    fun feed_div_has_title_plus_items() {
        val div = FeedDiv(Rss(rssWith2Items, subscription))

        assertThat(div.items.size, `is`(2))
    }

    @AfterEach
    fun unmock() = unmockkAll()

    @Test
    fun `full feed html`() {
        val clock = Clock.fixed(Instant.parse("2020-09-27T07:30:00Z"), ZoneId.of("+10:00"))
        mockkStatic("java.time.ZonedDateTime")
        every { ZonedDateTime.now() } returns ZonedDateTime.now(clock)

        val div = FeedDiv(Rss(rssWith2Items, subscription))

        assertThat(div.asHtml(), `is`("""
                |<div id="victoria_articles_feed">
                |<div class="feed_title">Victoria articles feed</div>
                |<div class="even" id="victoria_articles_feed-0">
                |    <table><tbody><tr>
                |    <td class="actions">
                |    <div><a href="#toc">Back to top</a></div>
                |    <div><a href="/read/ecQtfX6DhlbdNxbGorU4CBvRunw=/S703KZLSnJyg9pgrpB8toGqhEQg=">Mark read to here</a></div>
                |    </td>
                |    <td class="content">
                |    <div class="title_link"><a href="https://www.abc.net.au/news/2020-09-14/footage-shows-the-man-being-struck-by-a-police-car./12663386" target="S703KZLSnJyg9pgrpB8toGqhEQg=">Footage of a man being arrested by police in Melbourne's north.</a></div>
                |    <div>Footage of the man's arrest showed him running from police and striking a police car, before he was hit by a police car.</div>
                |    <div>Published at Mon, 14 Sep 2020 20:51:10 +1000</div>
                |    <div><p/></div>
                |    </td>
                |    </tr></tbody></table>
                |</div>
                |<div class="odd" id="victoria_articles_feed-1">
                |    <table><tbody><tr>
                |    <td class="actions">
                |    <div><a href="#toc">Back to top</a></div>
                |    <div><a href="/read/ecQtfX6DhlbdNxbGorU4CBvRunw=/qdmNgHhyl9deZuRho6wd8lssaDQ=">Mark read to here</a></div>
                |    </td>
                |    <td class="content">
                |    <div class="title_link"><a href="https://www.abc.net.au/news/2020-09-15/indigenous-communities-in-melbourne-spread-coronavirus-message/12662598" target="qdmNgHhyl9deZuRho6wd8lssaDQ=">How a 'heaven-sent' health worker is helping keep coronavirus cases low in the Indigenous community</a></div>
                |    <div><p>Indigenous Australians are particularly vulnerable to coronavirus, but Aboriginal health workers in Melbourne are using their intimate knowledge of their community to ensure critical health messages get through.</p></div>
                |    <div>Published at Sun, 27 Sep 2020 17:30:00 +1000</div>
                |    <div><p/></div>
                |    </td>
                |    </tr></tbody></table>
                |</div>
                |</div>
            """.trimMargin()))
    }
}
