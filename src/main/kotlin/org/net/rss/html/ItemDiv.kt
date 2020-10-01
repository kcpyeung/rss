package org.net.rss.html

import org.net.rss.Item

class ItemDiv(index: Int, private val item: Item, feedDiv: FeedDiv) {
    private val id = feedDiv.id + "-" + index

    fun asHtml(): String {
        return """
            <div id="${id}">
                <div><a href="${item.link}">${item.title}</a></div>
                <div>${item.description}</div>
                <div>Published at ${item.pubDate}</div>
                <div><p/></div>
            </div>
            
        """.trimIndent()
    }
}
