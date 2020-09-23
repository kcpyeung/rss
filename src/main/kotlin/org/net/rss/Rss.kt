package org.net.rss

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

class Rss(rss: String) {
    val doc: Document
    val category: String

    init {
        doc = getXmlDoc(rss)
        val tag = doc.getElementsByTagName("category")
        var c = "missing feed category"
        for (i in 0..tag.length - 1) {
            if (tag.item(i).nodeType == Node.ELEMENT_NODE) {
                c = tag.item(i).textContent
            }
        }
        category = c
    }

    fun getXmlDoc(rss: String): Document {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val xmlInput = InputSource(StringReader(rss))
        val doc = dBuilder.parse(xmlInput)

        doc.normalizeDocument()

        return doc
    }

}
