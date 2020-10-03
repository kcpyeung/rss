package org.net.rss

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.format.DateTimeFormatter

class RssTest {
    val subscription = Subscription("https://www.theguardian.com/au/rss", DateTimeFormatter.RFC_1123_DATE_TIME)

    val rss = """
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/"
     xmlns:media="http://search.yahoo.com/mrss/">
  <channel>
    <title>Victoria articles feed</title>
    <category>Australian Broadcasting Corporation: All content</category>
    <link>https://www.abc.net.au/news/</link>
    <description><![CDATA[
        rss.feed.description
      ]]></description>
    <language>en-AU</language>
    <copyright>Copyright 2020, Australian Broadcasting Corporation. All Rights Reserved.</copyright>
    <pubDate>Wed, 23 Sep 2020 10:45:16 +1000</pubDate>
    <lastBuildDate>Wed, 23 Sep 2020 10:45:16 +1000</lastBuildDate>
    <docs>http://blogs.law.harvard.edu/tech/rss</docs>
    <managingEditor>Newson1ine@abc.net.au (ABC News)</managingEditor>
    <image><title>ABC News</title><url>https://www.abc.net.au/news/image/8413416-1x1-144x144.png</url><link>https://www.abc.net.au/news/</link></image>
  </channel>
</rss>
""".trimIndent()

    @Test
    fun rss_has_category() {
        assertThat(Rss(rss, subscription).category, `is`("Australian Broadcasting Corporation: All content"))
    }

    @Test
    fun rss_has_title() {
        assertThat(Rss(rss, subscription).title, `is`("Victoria articles feed"))
    }

    @Test
    fun `rss id is base64-sha1 of its subscription url`() {
        assertThat(Rss(rss, subscription).id, `is`("ecQtfX6DhlbdNxbGorU4CBvRunw="))
    }
}