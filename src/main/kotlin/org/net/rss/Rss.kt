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
          .map { Item(it, subscription) }
          .sorted()
    }

    fun addItems(newItems: List<Item>) {
        val currentItems = this.items.toMutableSet()
        currentItems += newItems
        this.items = Collections.unmodifiableList(currentItems.toList().sorted())
    }

    fun deleteTo(guid: String) {
        val found = items.find { it.guid == guid } ?: return

        val itemCopy = items.toMutableList()
        val whereIsIt = itemCopy.indexOf(found)
        this.items = Collections.unmodifiableList(itemCopy.subList(whereIsIt + 1, itemCopy.size))
    }
}
