package org.net.rss

import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class Rss(rss: String) {
    private val doc: Document
    val category: String?
    val title: String?

    init {
        doc = getXmlDoc(rss)
        category = get("/rss/channel/category")
        title = get("/rss/channel/title")
    }

    private fun get(path: String): String? {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()

        val nodes = xPath.evaluate(path, doc, XPathConstants.NODESET) as NodeList
        return nodes.item(0)?.textContent
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
