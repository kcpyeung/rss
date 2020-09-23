package org.net.rss

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class MinimumItemTest {

    val rss = """
<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/"
     xmlns:media="http://search.yahoo.com/mrss/">
  <channel>
    <title>Victoria articles feed</title>
    <category>Australian Broadcasting Corporation: All content</category>
    <item>
        <title>Aboriginal elder denied entry to western Victorian protest sites she started</title>
        <link>https://www.abc.net.au/news/2020-09-23/elder-locked-out-of-protest-site-she-started/12685330</link>
        <description>
              <![CDATA[
              <p>An Aboriginal elder who has led a fight to protect trees from a highway project in western Victoria says she is embarrassed by rubbish left at the camps.</p>
              ]]>
        </description>
    </item>
  </channel>
</rss>
""".trimIndent()

    @Test fun item_has_title() {
        assertThat(Rss(rss).items[0].title, `is`("Aboriginal elder denied entry to western Victorian protest sites she started"))
    }
}
