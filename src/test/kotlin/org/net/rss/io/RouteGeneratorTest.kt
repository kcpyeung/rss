package org.net.rss.io

import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Test
import org.net.rss.config.Subscriptions

class RouteGeneratorTest {
    val yaml = """
            |-
            |   name: news
            |   subscriptions:
            |       -
            |           url: https://www.theguardian.com/au/rss
            |       -
            |           url: https://www.theage.com.au/rss/national/victoria.xml
            |-
            |   name: science
            |   subscriptions:
            |       -
            |           url: http://rss.sciam.com/basic-science
            |
            """.trimMargin()
    val subscriptions = Subscriptions(yaml)

    @Test
    fun `each subscription section becomes a path`() {
        val routes = RouteGenerator(subscriptions).routes

        assertThat(routes(Request(GET, "/news")), hasStatus(OK))
        assertThat(routes(Request(GET, "/science")), hasStatus(OK))
        assertThat(routes(Request(GET, "/gossip")), hasStatus(NOT_FOUND))
    }

    @Test
    fun `root path contains all news`() {
        val routes = RouteGenerator(subscriptions).routes

        assertThat(routes(Request(GET, "/")), hasStatus(OK))
    }

    @Test
    fun `read path marks items read`() {
        val routes = RouteGenerator(subscriptions).routes

        assertThat(routes(Request(GET, "/read/feed_id/item_id")), hasStatus(OK))
    }
}
