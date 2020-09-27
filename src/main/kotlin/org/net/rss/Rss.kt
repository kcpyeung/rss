package org.net.rss

import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class Rss(rss: String, dateFormat: DateTimeFormatter) {
    private val doc: Document
    val category: String?
    val title: String?
    val items = mutableListOf<Item>()

    init {
        doc = getXmlDoc(rss)
        category = getAsString("/rss/channel/category")
        title = getAsString("/rss/channel/title")

        val itemNodeList = get("/rss/channel/item")
        if (itemNodeList.length > 0) {
            for (i in 0..itemNodeList.length - 1) {
                items.add(Item(itemNodeList.item(i), dateFormat))
            }
        }
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
