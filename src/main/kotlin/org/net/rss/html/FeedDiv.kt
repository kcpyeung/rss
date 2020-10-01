package org.net.rss.html

import org.net.rss.Rss

class FeedDiv(rss: Rss) {
    val title = rss.title
    val id = title?.toLowerCase()?.replace(" ", "_")
    val children = rss.items.mapIndexed { index, item -> ItemDiv(index, item, this) }

    fun asHtml(): String {
        val buffer = StringBuilder(1024)
        buffer.append(start())
        for (child in children) {
            buffer.append(child.asHtml())
        }
        buffer.append(end())

        return buffer.toString()
    }

    private fun start(): String {
        return "<div id=\"${id}\">${title}\n"
    }

    private fun end(): String {
        return "</div>"
    }
}
