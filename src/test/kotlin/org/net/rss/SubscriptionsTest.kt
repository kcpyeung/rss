package org.net.rss

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
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
}