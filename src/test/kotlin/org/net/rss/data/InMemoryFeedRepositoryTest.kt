package org.net.rss.data

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.net.rss.Rss

class InMemoryFeedRepositoryTest {
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

    @Test
    fun `emtpy repo stores all items`() {
        val rss = Rss(rss1)
        val repo = InMemoryFeedRepository()

        repo.add("https://somenews.com", rss)

        assertThat(repo.hasSource("https://somenews.com"), `is`(true))
        assertThat(repo.get("https://somenews.com")?.items?.size, `is`(2))
    }
}