package org.net.rss.html

import org.net.rss.Rss

class FeedDiv(rss: Rss) {
    private val textContent = rss.title
    val id = textContent?.toLowerCase()?.replace(" ", "_")
    val children = rss.items.mapIndexed { index, item -> ItemDiv(index, item, this) }

    override fun toString(): String {
        val buffer = StringBuilder(1024)
        buffer.append(start())
        for (child in children) {
            buffer.append(child)
        }
        buffer.append(end())

        return buffer.toString()
    }

    private fun start(): String {
        return "<div id=\"${id}\">${textContent}"
    }

    private fun end(): String {
        return "</div>"
    }
}
