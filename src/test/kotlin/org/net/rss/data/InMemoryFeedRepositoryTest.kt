package org.net.rss.data

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.net.rss.Rss
import org.net.rss.Subscription
import java.time.format.DateTimeFormatter

class InMemoryFeedRepositoryTest {
    val subscription = Subscription("https://www.theguardian.com/au/rss", DateTimeFormatter.RFC_1123_DATE_TIME)

    private val rss1 = """
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/"
     xmlns:media="http://search.yahoo.com/mrss/">
  <channel>
    <title>Victoria articles feed</title>
    <category>Australian Broadcasting Corporation: All content</category>
    <item>
        <title>Title 1</title>
        <link>https://somenews.com/1</link>
        <description>description 1</description>
        <pubDate>Mon, 14 Sep 2020 20:50:00 +1000</pubDate>
    </item>
    <item>
        <title>Title 2</title>
        <link>https://somenews.com/2</link>
        <description>description 2</description>
        <pubDate>Mon, 14 Sep 2020 20:51:10 +1000</pubDate>
    </item>
  </channel>
</rss>
""".trimIndent()

    private val rss2WithNoOverlap = """
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/"
     xmlns:media="http://search.yahoo.com/mrss/">
  <channel>
    <title>Victoria articles feed</title>
    <category>Australian Broadcasting Corporation: All content</category>
    <item>
        <title>Title 3</title>
        <link>https://somenews.com/3</link>
        <description>description 3</description>
        <pubDate>Tue, 15 Sep 2020 20:50:00 +1000</pubDate>
    </item>
    <item>
        <title>Title 4</title>
        <link>https://somenews.com/4</link>
        <description>description 4</description>
        <pubDate>Tue, 15 Sep 2020 20:51:10 +1000</pubDate>
    </item>
  </channel>
</rss>
""".trimIndent()

    private val rss2WithOverlap = """
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/"
     xmlns:media="http://search.yahoo.com/mrss/">
  <channel>
    <title>Victoria articles feed</title>
    <category>Australian Broadcasting Corporation: All content</category>
    <item>
        <title>Title 2</title>
        <link>https://somenews.com/2</link>
        <description>description 2</description>
        <pubDate>Mon, 14 Sep 2020 20:51:10 +1000</pubDate>
    </item>
    <item>
        <title>Title 4</title>
        <link>https://somenews.com/4</link>
        <description>description 4</description>
        <pubDate>Tue, 15 Sep 2020 20:51:10 +1000</pubDate>
    </item>
  </channel>
</rss>
""".trimIndent()

    @BeforeEach
    fun emptyRepo() {
        InMemoryFeedRepository.clear()
    }

    @Test
    fun `emtpy repo stores all items`() {
        val rss = Rss(rss1, subscription)

        InMemoryFeedRepository.add("https://somenews.com", rss)

        assertThat(InMemoryFeedRepository.hasSource("https://somenews.com"), `is`(true))
        assertThat(InMemoryFeedRepository.get("https://somenews.com")?.items?.size, `is`(2))
    }

    @Test
    fun `adding new items to existing feed appends if no overlap`() {
        InMemoryFeedRepository.add("https://somenews.com", Rss(rss1, subscription))

        InMemoryFeedRepository.add("https://somenews.com", Rss(rss2WithNoOverlap, subscription))

        assertThat(InMemoryFeedRepository.get("https://somenews.com")?.items?.size, `is`(4))
    }

    @Test
    fun `filters off overlap and adds new items to existing feed`() {
        InMemoryFeedRepository.add("https://somenews.com", Rss(rss1, subscription))

        InMemoryFeedRepository.add("https://somenews.com", Rss(rss2WithOverlap, subscription))
        val items = InMemoryFeedRepository.get("https://somenews.com")?.items

        assertThat(items?.size, `is`(3))
        assertThat(items?.map { it.title }, `is`(listOf("Title 1", "Title 2", "Title 4")))
    }
}
