package org.net.rss.html

import org.net.rss.Rss

class FeedDiv(rss: Rss) {
    val hash = rss.id
    val title = rss.title
    val id = title?.toLowerCase()?.replace(" ", "_")
    val items = rss.items.mapIndexed { index, item -> ItemDiv(index, item, this) }

    fun asHtml(): String {
        val buffer = StringBuilder(1024)
        buffer.append(start())
        buffer.append(title())
        for (item in items) {
            buffer.append(item.asHtml())
        }
        buffer.append(end())

        return buffer.toString()
    }

    private fun title(): String {
        return "<div class=\"feed_title\">${title}</div>\n"
    }

    private fun start(): String {
        return "<div id=\"${id}\">\n"
    }

    private fun end(): String {
        return "</div>"
    }
}
