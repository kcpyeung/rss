package org.net.rss

import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class Item(private val node: Node) {
    val title = getAsString("title")
    val link = getAsString("link")
    val description = getAsString("description")
    val pubDate = getAsString("pubDate") ?: DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now())

    private fun get(path: String): NodeList {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()

        return xPath.evaluate(path, node, XPathConstants.NODESET) as NodeList
    }

    private fun getAsString(path: String): String? {
        return get(path).item(0)?.textContent?.trim()
    }
}
