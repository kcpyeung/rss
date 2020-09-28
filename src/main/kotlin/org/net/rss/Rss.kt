package org.net.rss

import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class Rss(rss: String, dateFormat: DateTimeFormatter) {
    private val doc: Document
    val category: String?
    val title: String?
    var items: List<Item>

    init {
        doc = getXmlDoc(rss)
        category = getAsString("/rss/channel/category")
        title = getAsString("/rss/channel/title")

        val itemNodeList = get("/rss/channel/item")
        val list = mutableListOf<Item>()
        if (itemNodeList.length > 0) {
            for (i in 0..itemNodeList.length - 1) {
                list.add(Item(itemNodeList.item(i), dateFormat))
            }
        }
        items = Collections.unmodifiableList(list.sorted())
    }

    fun addItems(newItems: List<Item>) {
        val currentItems = this.items.toMutableSet()
        currentItems.addAll(newItems)
        this.items = currentItems.toList().sorted()
    }

    private fun get(path: String): NodeList {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()

        return xPath.evaluate(path, doc, XPathConstants.NODESET) as NodeList
    }

    private fun getAsString(path: String): String? {
        return get(path).item(0)?.textContent
    }

    private fun getXmlDoc(rss: String): Document {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val xmlInput = InputSource(StringReader(rss))
        val doc = dBuilder.parse(xmlInput)

        doc.normalizeDocument()

        return doc
    }
}
