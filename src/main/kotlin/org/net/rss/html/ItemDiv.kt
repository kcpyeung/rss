package org.net.rss.html

import org.net.rss.Item

class ItemDiv(index: Int, item: Item, feedDiv: FeedDiv) {
    val id = feedDiv.id + "-" + index
}
