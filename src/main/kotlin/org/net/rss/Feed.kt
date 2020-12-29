package org.net.rss

import java.io.Serializable
import java.time.ZonedDateTime
import java.util.*

interface Feed : Serializable {
    val id: String
    val title: String?
    var items: List<Item>

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

    fun safeCopy(items: List<Item>) {
        synchronized(this) {
            this.items = Collections.unmodifiableList(items.sorted())
        }
    }
}
