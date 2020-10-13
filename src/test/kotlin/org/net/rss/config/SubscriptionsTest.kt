package org.net.rss.config

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SubscriptionsTest {
    @Test
    fun `Subscriptions-dot-all is retained for backward-compatibility`() {
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

        assertThat(subscriptions.sections.size, `is`(2))

        assertThat(subscriptions.all.size, `is`(3))
        assertThat(subscriptions.all[0].url, `is`("https://www.theguardian.com/au/rss"))
        assertThat(subscriptions.all[1].url, `is`("https://www.theage.com.au/rss/national/victoria.xml"))
        assertThat(subscriptions.all[2].url, `is`("http://rss.sciam.com/basic-science"))
    }

    @Test
    fun `sections group subscriptions`() {
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

        assertThat(subscriptions.sections[0].name, `is`("news"))
        assertThat(subscriptions.sections[0].subscriptions[0].url, `is`("https://www.theguardian.com/au/rss"))
        assertThat(subscriptions.sections[0].subscriptions[1].url, `is`("https://www.theage.com.au/rss/national/victoria.xml"))

        assertThat(subscriptions.sections[1].name, `is`("science"))
        assertThat(subscriptions.sections[1].subscriptions[0].url, `is`("http://rss.sciam.com/basic-science"))
    }

    @Test
    fun `yaml can specify title`() {
        val yaml = """
            |-
            |   name: news
            |   subscriptions:
            |      -
            |          url: https://www.theguardian.com/au/rss
            |          title: Title I want to see
            |      -
            |          url: https://www.theage.com.au/rss/national/victoria.xml
            |
            """.trimMargin()

        val subscriptions = Subscriptions(yaml)

        assertThat(subscriptions.all[0].title, `is`("Title I want to see"))
        assertThat(subscriptions.all[1].title, `is`(nullValue()))
    }

    @Nested
    inner class LinkRewriteTests {
        @Test
        fun `linkRewriteFactoryClass specifies the link rewriter factory class`() {
            val yaml = """
            |-
            |   name: news
            |   subscriptions:
            |       -
            |           url: https://www.theguardian.com/au/rss
            |           linkRewriteFactoryClass: org.net.rss.config.HelloLinkRewriterFactory
            |
            """.trimMargin()

            val subscriptions = Subscriptions(yaml)

            assertThat(subscriptions.all[0].linkRewrite("byebye"), `is`("hello byebye"))
        }

        @Test
        fun `missing linkRewriteFactoryClass defaults to identity function`() {
            val yaml = """
            |-
            |   name: news
            |   subscriptions:
            |       -
            |           url: https://www.theguardian.com/au/rss
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
