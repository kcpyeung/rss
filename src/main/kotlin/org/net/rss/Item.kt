package org.net.rss

import java.security.MessageDigest
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

class Item(getAsString: (String) -> String?, dateFormat: DateTimeFormatter) : Comparable<Item> {
    val title = getAsString("title")
    val link = getAsString("link")
    val description = getAsString("description")
    val pubDate = if (getAsString("pubDate") == null) ZonedDateTime.now() else ZonedDateTime.parse(getAsString("pubDate"), dateFormat)
    val guid = getAsString("guid") ?: sha1()

    private fun sha1(): String {
        val input = "${title}:${link}:${description}:${pubDate}"
        val digest = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())

        return Base64.getEncoder().encodeToString(digest)
    }

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
