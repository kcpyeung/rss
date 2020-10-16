package org.net.rss.io

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.hamkrest.hasHeader
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.net.rss.config.Section
import org.net.rss.config.Subscriptions

class RouteGeneratorTest {
    private fun subscriptionsOf(vararg sections: Section): Subscriptions {
        val subscriptions = mockk<Subscriptions>()
        every { subscriptions.sections } returns sections.toList()

        return subscriptions
    }

    private fun section(name: String, unread: Boolean): Section {
        val section = mockk<Section>(relaxed = true)
        every { section.hasUnreadItems() } returns unread
        every { section.name } returns name

        return section
    }

    @Nested
    inner class WhereAPathGoes {
        @Test
        fun `each section goes to a page`() {
            val subscriptions = subscriptionsOf(section("news", unread = true), section("science", unread = true))
            val routes = RouteGenerator(subscriptions).routes

            assertThat(routes(Request(GET, "/news")), hasStatus(OK))
            assertThat(routes(Request(GET, "/science")), hasStatus(OK))
            assertThat(routes(Request(GET, "/gossip")), hasStatus(NOT_FOUND))
        }

        @Test
        fun `a fully-read section redirects to home page`() {
            val subscriptions = subscriptionsOf(section("news", unread = false), section("science", unread = true))
            val routes = RouteGenerator(subscriptions).routes

            assertThat(routes(Request(GET, "/news")), hasStatus(SEE_OTHER).and(hasHeader("Location", "/")))
            assertThat(routes(Request(GET, "/science")), hasStatus(OK))
            assertThat(routes(Request(GET, "/gossip")), hasStatus(NOT_FOUND))
        }
    }

    @Test
    fun `root path contains all news`() {
        val subscriptions = subscriptionsOf(section("news", unread = true), section("science", unread = true))
        val routes = RouteGenerator(subscriptions).routes

        assertThat(routes(Request(GET, "/")), hasStatus(OK))
    }

    @Test
    fun `read path marks items read`() {
        val subscriptions = subscriptionsOf(section("news", unread = true), section("science", unread = true))
        val routes = RouteGenerator(subscriptions).routes

        assertThat(routes(Request(GET, "/read/feed_id/item_id")), hasStatus(SEE_OTHER))
    }
}
