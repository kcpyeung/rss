package org.net.rss

import org.net.rss.config.Subscription
import java.time.ZonedDateTime

class Item(getAsString: (String) -> String?, subscription: Subscription) : Comparable<Item> {
    val title = getAsString("title")
    val link = if (getAsString("link") != null) subscription.linkRewrite(getAsString("link")!!) else null
    val description = getAsString("description")
    val content = getAsString("content")
    val pubDate = if (getAsString("pubDate") == null) ZonedDateTime.now() else ZonedDateTime.parse(getAsString("pubDate"), subscription.dateFormat)
    val guid = subscription.itemIdGen("${title}:${link}:${description}")

    override fun compareTo(other: Item): Int {
        val dateComparison = this.pubDate.compareTo(other.pubDate)
        if (dateComparison != 0) {
            return dateComparison
        } else {
            return this.guid.compareTo(other.guid)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Item

        if (guid != other.guid) return false

        return true
    }

    override fun hashCode(): Int {
        return guid.hashCode()
    }
}
