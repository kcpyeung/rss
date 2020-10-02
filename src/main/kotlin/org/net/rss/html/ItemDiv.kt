package org.net.rss.html

import org.net.rss.Item
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ItemDiv(index: Int, private val item: Item, feedDiv: FeedDiv) {
    private val df = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("Australia/Melbourne"))
    private val id = feedDiv.id + "-" + index
    private val clazz = if (index.rem(2) == 0) "even" else "odd"

    fun asHtml(): String {
        return """|<div class="${clazz}" id="${id}">
            |    <table><tbody><tr>
            |    ${actions()}
            |    ${content()}
            |    </tr></tbody></table>
            |</div>
            |""".trimMargin()
    }

    private fun content(): String {
        return """|<td>
                |    <div class="title_link"><a href="${item.link}">${item.title}</a></div>
                |    <div>${item.description}</div>
                |    <div>Published at ${df.format(item.pubDate)}</div>
                |    <div><p/></div>
                |    </td>""".trimMargin()
    }

    private fun actions(): String {
        return """|<td>
                |    <div><a href="#toc">Back to top</a></div>
                |    </td>""".trimMargin()
    }
}
