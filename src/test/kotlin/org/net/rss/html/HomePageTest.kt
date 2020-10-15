package org.net.rss.html

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.net.rss.config.Section
import org.net.rss.config.Subscriptions

class HomePageTest {
    @Test
    fun `home page links to all unread sections`() {
        val subscriptions = subscriptionsOf(unread("news"), unread("jokes"), unread("science"))

        val homepage = HomePage(subscriptions)

        assertThat(homepage.links(), `is`(listOf(
          "<a href=\"/news\">news</a>",
          "<a href=\"/jokes\">jokes</a>",
          "<a href=\"/science\">science</a>")))
    }

    @Test
    fun `home page does not link to read sections`() {
        val subscriptions = subscriptionsOf(read("news"), unread("jokes"), unread("science"))

        val homepage = HomePage(subscriptions)

        assertThat(homepage.links(), `is`(listOf(
          "news",
          "<a href=\"/jokes\">jokes</a>",
          "<a href=\"/science\">science</a>")))
    }

    private fun read(name: String): Section {
        val section = mockk<Section>()
        every { section.name } returns name
        every { section.hasUnreadItems() } returns false

        return section
    }

    private fun unread(name: String): Section {
        val section = mockk<Section>()
        every { section.name } returns name
        every { section.hasUnreadItems() } returns true

        return section
    }

    private fun subscriptionsOf(vararg sections: Section): Subscriptions {
        val subscriptions = mockk<Subscriptions>()
        every { subscriptions.sections } returns sections.toList()

        return subscriptions
    }
}
