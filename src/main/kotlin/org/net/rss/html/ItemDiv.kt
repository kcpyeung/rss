package org.net.rss.html

import org.net.rss.Item
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ItemDiv(index: Int, private val item: Item, private val feedDiv: FeedDiv) {
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
        return """|<td class="content">
                |    <div><a class="title_link" href="${item.link}" target="${item.guid}">${item.title}</a></div>
                |    <div>${item.description}</div>
                |    """.trimMargin() +
          detailText() +
             """|<div>Published at ${df.format(item.pubDate)}</div>
                |    <div><p/></div>
                |    </td>""".trimMargin()
    }

    private fun detailText(): String {
        return if (item.content != null)
            """|<div onclick="document.getElementById('${item.guid}').setAttribute('style', 'display: block')"><b>More >>></b></div>
                 |    <div id="${item.guid}" style="display: none">${item.content}</div>
                 |    
              """.trimMargin() else ""
    }

    private fun actions(): String {
        return """|<td class="actions">
                |    <div><a href="#toc">Back to top</a></div>
                |    <div><a href="/read/${feedDiv.hash}/${item.guid}">Mark read to here</a></div>
                |    </td>""".trimMargin()
    }
}
