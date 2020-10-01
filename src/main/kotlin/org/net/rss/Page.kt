package org.net.rss

import org.net.rss.html.FeedDiv

class Page(private val feedDivs: List<FeedDiv>) {
    private val divs = join(feedDivs.map { it.asHtml() })

    private fun toc(): String {
        val feedLinks =
          join(feedDivs.map { """<div><b><a href="#${it.id}">${it.title}</a></b></div><p/>""" })

        return """
            <div id="toc">
            ${feedLinks}
            </div>
        """.trimIndent()
    }

    fun asHtml(): String {
        return "<html><body>${toc()}$divs</body></html>"
    }

    private fun join(stuff: List<String>): String {
        return stuff.joinToString(separator = "")
    }
}
