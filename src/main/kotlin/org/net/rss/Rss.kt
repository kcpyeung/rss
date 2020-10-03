package org.net.rss

import org.net.rss.xml.XmlHelper
import java.util.*

class Rss(rss: String, subscription: Subscription) {
    val id = subscription.feedIdGen(subscription.url)
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
        if (this.items.isNotEmpty()) {
            val currentItems = this.items.toMutableSet()
            val lastOfCurrentItems = currentItems.last()

            currentItems += newItems.filter { it.pubDate >= lastOfCurrentItems.pubDate }

            safeCopy(currentItems.toList())
        } else {
            safeCopy(newItems)
        }
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
