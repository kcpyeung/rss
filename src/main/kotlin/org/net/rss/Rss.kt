package org.net.rss

import org.net.rss.config.Subscription
import org.net.rss.xml.XmlHelper
import java.time.ZonedDateTime
import java.util.*

class Rss(rss: String, subscription: Subscription) : Feed {
    override val id = subscription.feedIdGen(subscription.url)
    override val title: String?
    override lateinit var items: List<Item>

    init {
        val xmlHelper = XmlHelper(rss)

        title = xmlHelper.getAsString("/rss/channel/title")
        safeCopy(xmlHelper
          .getRssLookups("/rss/channel/item")
          .map { Item(it, subscription) })
    }

    fun addItems(newItems: List<Item>, lastRefreshedAt: ZonedDateTime) {
        val newItemsToAdd = newItems.filter { it.pubDate >= lastRefreshedAt }
        if (this.items.isNotEmpty()) {
            val currentItems = this.items.toMutableSet()
            currentItems += newItemsToAdd

            safeCopy(currentItems.toList())
        } else {
            safeCopy(newItemsToAdd)
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
