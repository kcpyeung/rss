package org.net.rss

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class ItemTest {

    val rss = """
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/"
     xmlns:media="http://search.yahoo.com/mrss/">
  <channel>
    <title>Victoria articles feed</title>
    <category>Australian Broadcasting Corporation: All content</category>
    <item>
        <title>How a &#039;heaven-sent&#039; health worker is helping keep coronavirus cases low in the Indigenous community</title>
        <link>https://www.abc.net.au/news/2020-09-15/indigenous-communities-in-melbourne-spread-coronavirus-message/12662598</link>
        <description>
              <![CDATA[
              <p>Indigenous Australians are particularly vulnerable to coronavirus, but Aboriginal health workers in Melbourne are using their intimate knowledge of their community to ensure critical health messages get through.</p>
              ]]>
        </description>
    </item>
  </channel>
</rss>
""".trimIndent()

    @Test
    fun item_has_basic_properties() {
        val item = Rss(rss).items[0]

        assertThat(item.description, `is`("<p>Indigenous Australians are particularly vulnerable to coronavirus, but Aboriginal health workers in Melbourne are using their intimate knowledge of their community to ensure critical health messages get through.</p>"))
        assertThat(item.link, `is`("https://www.abc.net.au/news/2020-09-15/indigenous-communities-in-melbourne-spread-coronavirus-message/12662598"))
        assertThat(item.title, `is`("How a 'heaven-sent' health worker is helping keep coronavirus cases low in the Indigenous community"))
    }
}
