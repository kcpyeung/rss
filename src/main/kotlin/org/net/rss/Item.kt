package org.net.rss

import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class Item(private val node: Node, dateFormat: DateTimeFormatter): Comparable<Item> {
    val guid = getAsString("guid") ?: UUID.randomUUID().toString()
    val title = getAsString("title")
    val link = getAsString("link")
    val description = getAsString("description")
    val pubDate = if (getAsString("pubDate") == null) ZonedDateTime.now() else ZonedDateTime.parse(getAsString("pubDate"), dateFormat)

    private fun get(path: String): NodeList {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()

        return xPath.evaluate(path, node, XPathConstants.NODESET) as NodeList
    }

    private fun getAsString(path: String): String? {
        return get(path).item(0)?.textContent?.trim()
    }

    override fun compareTo(other: Item): Int {
        val dateComparison = this.pubDate.compareTo(other.pubDate)
        if (dateComparison != 0) {
            return dateComparison
        } else {
            return this.guid.compareTo(other.guid)
        }
    }
}
