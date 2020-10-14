package org.net.rss.html

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.net.rss.config.Section
import org.net.rss.config.Subscriptions

class HomeSectionPageTest {
    @Test
    fun `home page links to all sections`() {
        val subscriptions = mockSubscriptions()

        val homepage = HomePage(subscriptions)

        assertThat(homepage.links, `is`(listOf(
          "<a href=\"/news\">news</a>",
          "<a href=\"/jokes\">jokes</a>",
          "<a href=\"/science\">science</a>")))
    }

    private fun mockSubscriptions(): Subscriptions {
        val s1 = mockk<Section>()
        every { s1.name } returns "news"
        val s2 = mockk<Section>()
        every { s2.name } returns "jokes"
        val s3 = mockk<Section>()
        every { s3.name } returns "science"

        val subscriptions = mockk<Subscriptions>()
        every { subscriptions.sections } returns listOf(s1, s2, s3)

        return subscriptions
    }
}
