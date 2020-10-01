package org.net.rss.html

import org.net.rss.Item

class ItemDiv(index: Int, private val item: Item, feedDiv: FeedDiv) {
    private val id = feedDiv.id + "-" + index
    private val clazz = if (index.rem(2) == 0) "even" else "odd"

    fun asHtml(): String {
        return """
            <div class="${clazz}" id="${id}">
                <div><a href="${item.link}">${item.title}</a></div>
                <div>${item.description}</div>
                <div>Published at ${item.pubDate}</div>
                <div><p/></div>
            </div>
            
        """.trimIndent()
    }
}
