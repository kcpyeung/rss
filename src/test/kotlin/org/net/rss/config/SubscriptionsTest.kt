package org.net.rss.config

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubscriptionsTest {
    @Test
    fun `parses url from yaml`() {
        val yaml = """
            |-
            |   url: https://www.theguardian.com/au/rss
            |-
            |   url: https://www.theage.com.au/rss/national/victoria.xml
            |
            """.trimMargin()

        val subscriptions = Subscriptions(yaml)

        assertThat(subscriptions.all.size, `is`(2))
        assertThat(subscriptions.all[0].url, `is`("https://www.theguardian.com/au/rss"))
        assertThat(subscriptions.all[1].url, `is`("https://www.theage.com.au/rss/national/victoria.xml"))
    }

    @Nested
    inner class LinkRewriteTests {
        @Test
        fun `linkRewriteFactoryClass specifies the link rewriter factory class`() {
            val yaml = """
            |-
            |   url: https://www.theguardian.com/au/rss
            |   linkRewriteFactoryClass: org.net.rss.config.HelloLinkRewriterFactory
            |
            """.trimMargin()

            val subscriptions = Subscriptions(yaml)

            assertThat(subscriptions.all[0].linkRewrite("byebye"), `is`("hello byebye"))
        }

        @Test
        fun `missing linkRewriteFactoryClass defaults to identity function`() {
            val yaml = """
            |-
            |   url: https://www.theguardian.com/au/rss
            |
            """.trimMargin()

            val subscriptions = Subscriptions(yaml)

            assertThat(subscriptions.all[0].linkRewrite("byebye"), `is`("byebye"))
        }
    }
}

class HelloLinkRewriterFactory : LinkRewriterFactory {
    override fun get(): (String) -> String {
        return { "hello $it" }
    }
}
