package org.net.rss.xml

import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class XmlHelper(xml: String) {
    private val doc = getXmlDoc(xml)

    fun getAsString(path: String): String? {
        return getAsString0(doc, path)
    }

    fun getRssLookups(path: String): List<(String) -> String?> {
        val nodeList = get(doc, path)
        val list = arrayListOf<(String) -> String?>()
        if (nodeList.length > 0) {
            for (i in 0 until nodeList.length) {
                val node = nodeList.item(i)
                list += { getAsString0(node, it) }
            }
        }

        return Collections.unmodifiableList(list)
    }

    fun getAtomLookups(path: String): List<(String) -> String?> {
        val nodeList = get(doc, path)
        val list = arrayListOf<(String) -> String?>()
        if (nodeList.length > 0) {
            for (i in 0 until nodeList.length) {
                val node = nodeList.item(i)
                list += { getAsStringAtom(node, it) }
            }
        }

        return Collections.unmodifiableList(list)
    }

    private fun get(node: Node, path: String): NodeList {
        val xpFactory = XPathFactory.newInstance()
        val xPath = xpFactory.newXPath()

        return xPath.evaluate(path, node, XPathConstants.NODESET) as NodeList
    }

    private fun getAsString0(node: Node, path: String): String? {
        return get(node, path).item(0)?.textContent?.trim()
    }

    private fun getAsStringAtom(node: Node, path: String): String? {
        if (path == "description") {
            val content = getAsString0(node, "content")
            val summary = getAsString0(node, "summary")

            if (content == null) return summary
            if (summary == null) return content
            return "${content}\n${summary}"
        }
        if (path == "pubDate") return getAsString0(node, "updated")
        if (path == "link") {
            return get(node, path)
              .item(0)
              ?.attributes
              ?.getNamedItem("href")
              ?.textContent
              ?.trim()
        }

        return getAsString0(node, path)
    }

    private fun getXmlDoc(rss: String): Node {
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val xmlInput = InputSource(StringReader(rss))
        val doc = dBuilder.parse(xmlInput)

        doc.normalizeDocument()

        return doc
    }
}
