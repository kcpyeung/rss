package org.net.rss.html

import org.net.rss.Item

class ItemDiv(index: Int, private val item: Item, feedDiv: FeedDiv) {
    val id = feedDiv.id + "-" + index

    override fun toString(): String {
        val buffer = StringBuffer(1024)

        buffer.append("<div id=\"${id}\">").append("\n")
        buffer.append("    <div><a href=\"${link()}\">${title()}</a></div>").append("\n")
        buffer.append("    <div>${description()}</div>").append("\n")
        buffer.append("</div>").append("\n")

        return buffer.toString()
    }

    private fun description(): String? {
        return item.description
    }

    private fun title(): String? {
        return item.title
    }

    private fun link(): String? {
        return item.link
    }
}
