package org.net.rss.html

import org.net.rss.Item

class ItemDiv(index: Int, private val item: Item, feedDiv: FeedDiv) {
    private val id = feedDiv.id + "-" + index

    override fun toString(): String {
        val buffer = StringBuffer(1024)

        buffer.append("<div id=\"${id}\">").append("\n")
        buffer.append("    <div><a href=\"${item.link}\">${item.title}</a></div>").append("\n")
        buffer.append("    <div>${item.description}</div>").append("\n")
        buffer.append("    <div>Published at ${item.pubDate}</div>").append("\n")
        buffer.append("    <div><p/></div>").append("\n")
        buffer.append("</div>").append("\n")

        return buffer.toString()
    }
}
