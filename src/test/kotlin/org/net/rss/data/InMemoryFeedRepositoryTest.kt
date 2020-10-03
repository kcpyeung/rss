package org.net.rss.data

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.net.rss.Rss
import org.net.rss.Subscription
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class InMemoryFeedRepositoryTest {
    val subscription = Subscription("https://www.theguardian.com/au/rss", feedIdGen = { _ -> "id" })

    private val rss1 = """
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/"
     xmlns:media="http://search.yahoo.com/mrss/">
  <channel>
    <title>Victoria articles feed</title>
    <category>Australian Broadcasting Corporation: All content</category>
    <item>
        <guid>guid1</guid>
        <title>Title 1</title>
        <link>https://somenews.com/1</link>
        <description>description 1</description>
        <pubDate>Mon, 14 Sep 2020 20:50:00 +1000</pubDate>
    </item>
    <item>
        <guid>guid2</guid>
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
        <guid>guid3</guid>
        <title>Title 3</title>
        <link>https://somenews.com/3</link>
        <description>description 3</description>
        <pubDate>Tue, 15 Sep 2020 20:50:00 +1000</pubDate>
    </item>
    <item>
        <guid>guid4</guid>
        <title>Title 4</title>
        <link>https://somenews.com/4</link>
        <description>description 4</description>
        <pubDate>Tue, 15 Sep 2020 20:51:10 +1000</pubDate>
    </item>
  </channel>
</rss>
""".trimIndent()

    private val rss1Updated = """
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/"
     xmlns:media="http://search.yahoo.com/mrss/">
  <channel>
    <title>Victoria articles feed</title>
    <category>Australian Broadcasting Corporation: All content</category>
    <item>
        <guid>guid1</guid>
        <title>Title 1</title>
        <link>https://somenews.com/1</link>
        <description>description 1</description>
        <pubDate>Mon, 14 Sep 2020 20:50:00 +1000</pubDate>
    </item>
    <item>
        <guid>guid2</guid>
        <title>Title 2</title>
        <link>https://somenews.com/2</link>
        <description>description 2</description>
        <pubDate>Mon, 14 Sep 2020 20:51:10 +1000</pubDate>
    </item>
    <item>
        <guid>guid3</guid>
        <title>Title 3</title>
        <link>https://somenews.com/3</link>
        <description>description 3</description>
        <pubDate>Tue, 15 Sep 2020 20:52:10 +1000</pubDate>
    </item>
    <item>
        <guid>guid4</guid>
        <title>Title 4</title>
        <link>https://somenews.com/4</link>
        <description>description 4</description>
        <pubDate>Tue, 15 Sep 2020 20:53:10 +1000</pubDate>
    </item>
  </channel>
</rss>
""".trimIndent()

    @BeforeEach
    fun emptyRepo() {
        InMemoryFeedRepository.clear()
        unmockkAll()
        mockkStatic("java.time.ZonedDateTime")
    }

    @Test
    fun `empty repo stores all items`() {
        val rss = Rss(rss1, subscription)

        InMemoryFeedRepository.add(rss)

        assertThat(InMemoryFeedRepository.hasSource("id"), `is`(true))
        assertThat(InMemoryFeedRepository.get("id")?.items?.size, `is`(2))
    }

    @Test
    fun `adding new items to existing feed appends if no overlap`() {
        atT1 {
            InMemoryFeedRepository.add(Rss(rss1, subscription))
        }

        atT2 {
            InMemoryFeedRepository.add(Rss(rss2WithNoOverlap, subscription))
        }

        assertThat(InMemoryFeedRepository.get("id")?.items?.size, `is`(4))
    }

    private fun atT1(whatHappened: () -> Unit) {
        every { ZonedDateTime.now(any<ZoneId>()) } returns ZonedDateTime.now(Clock.fixed(Instant.parse("2020-09-14T21:00:00Z"), ZoneId.of("+10:00")))
        whatHappened.invoke()
    }

    private fun atT2(whatHappened: () -> Unit) {
        every { ZonedDateTime.now(any<ZoneId>()) } returns ZonedDateTime.now(Clock.fixed(Instant.parse("2020-09-14T23:59:59Z"), ZoneId.of("+10:00")))
        whatHappened.invoke()
    }

    @Test
    fun `deleted items will not be added back at subsequent refreshes`() {
        atT1 {
            val rss = Rss(rss1, subscription)
            InMemoryFeedRepository.add(rss)
            InMemoryFeedRepository.deleteTo("id", rss.items[0].guid)
            val afterDelete = InMemoryFeedRepository.get("id")?.items?.map { it.title }
            assertThat(afterDelete, `is`(listOf("Title 2")))
        }

        atT2 {
            val rssAtNextRefresh = Rss(rss1Updated, subscription)
            InMemoryFeedRepository.add(rssAtNextRefresh)
        }

        val afterRefresh = InMemoryFeedRepository.get("id")?.items?.map { it.title }
        assertThat(afterRefresh, `is`(listOf("Title 2", "Title 3", "Title 4")))
    }

    @Nested
    inner class Delete {
        @BeforeEach
        private fun populate() {
            InMemoryFeedRepository.add(Rss(rss1Updated, subscription))
        }

        @Test
        fun `deleting a nonexistent item has no effect`() {
            val beforeDelete = InMemoryFeedRepository.get("id")?.items
            assertThat(beforeDelete?.map { it.title }, `is`(listOf("Title 1", "Title 2", "Title 3", "Title 4")))

            InMemoryFeedRepository.deleteTo("id", "abcde")

            val afterDelete = InMemoryFeedRepository.get("id")?.items
            assertThat(afterDelete?.map { it.title }, `is`(listOf("Title 1", "Title 2", "Title 3", "Title 4")))
        }

        @Test
        fun `deleting items before and up to guid by time`() {
            val beforeDelete = InMemoryFeedRepository.get("id")?.items
            assertThat(beforeDelete?.map { it.title }, `is`(listOf("Title 1", "Title 2", "Title 3", "Title 4")))

            val deleteToItem = beforeDelete?.get(2)
            InMemoryFeedRepository.deleteTo("id", deleteToItem!!.guid)

            val afterDelete = InMemoryFeedRepository.get("id")?.items
            assertThat(afterDelete?.map { it.title }, `is`(listOf("Title 4")))
        }

        @Test
        fun `deleting all, then refreshing the same feed will not add read items`() {
            val beforeDelete = InMemoryFeedRepository.get("id")?.items
            assertThat(beforeDelete?.map { it.title }, `is`(listOf("Title 1", "Title 2", "Title 3", "Title 4")))

            val deleteToItem = beforeDelete?.last()
            InMemoryFeedRepository.deleteTo("id", deleteToItem!!.guid)

            InMemoryFeedRepository.add(Rss(rss1Updated, subscription))

            val afterDelete = InMemoryFeedRepository.get("id")?.items
            assertThat(afterDelete?.isEmpty(), `is`(true))
        }
    }
}
