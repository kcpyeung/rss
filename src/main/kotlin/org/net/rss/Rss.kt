package org.net.rss

import org.net.rss.xml.XmlHelper
import java.util.*

class Rss(rss: String, subscription: Subscription) {
    val category: String?
    val title: String?
    var items: List<Item>

    init {
        val xmlHelper = XmlHelper(rss)

        category = xmlHelper.getAsString("/rss/channel/category")
        title = xmlHelper.getAsString("/rss/channel/title")
        items = xmlHelper
          .getLookups("/rss/channel/item")
          .map { Item(it, subscription.dateFormat, subscription.linkRewrite) }
          .sorted()
    }

    fun addItems(newItems: List<Item>) {
        val currentItems = this.items.toMutableSet()
        currentItems += newItems
        this.items = Collections.unmodifiableList(currentItems.toList().sorted())
    }
}
