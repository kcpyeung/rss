package org.net.rss

import org.net.rss.xml.XmlHelper
import java.util.*

class Rss(rss: String, subscription: Subscription) {
    val id = subscription.idGenerator(subscription.url)
    val category: String?
    val title: String?
    lateinit var items: List<Item>

    init {
        val xmlHelper = XmlHelper(rss)

        category = xmlHelper.getAsString("/rss/channel/category")
        title = xmlHelper.getAsString("/rss/channel/title")
        safeCopy(xmlHelper
          .getLookups("/rss/channel/item")
          .map { Item(it, subscription) })
    }

    fun addItems(newItems: List<Item>) {
        val currentItems = this.items.toMutableSet()
        currentItems += newItems

        safeCopy(currentItems.toList())
    }

    fun deleteTo(guid: String) {
        val whereIsIt = items.indexOfFirst { it.guid == guid }
        if (whereIsIt == -1) return

        safeCopy(items.subList(whereIsIt + 1, items.size))
    }

    private fun safeCopy(items: List<Item>) {
        synchronized(this) {
            this.items = Collections.unmodifiableList(items.sorted())
        }
    }
}
