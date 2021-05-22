package org.net.rss.html

class SectionPage(private val feedDivs: List<FeedDiv>) {
    private val divs = join(feedDivs.map { it.asHtml() })

    private fun toc(): String {
        val feedLinks =
          join(feedDivs.map { """<div><b>${titleOrLink(it)}</b></div><p/>""" })

        return """
            <div id="toc">
            <div id="main"><h1><a href="/">Main</a></h1></div>
            ${feedLinks}
            </div>
        """.trimIndent()
    }

    private fun titleOrLink(feedDiv: FeedDiv): String {
        if (feedDiv.items.isEmpty()) {
            return feedDiv.title!!
        } else {
            return """<a href="#${feedDiv.id}">${feedDiv.title}</a>"""
        }
    }

    fun asHtml(): String {
        return "<html><head><style>${style()}</style></head><body>${toc()}$divs</body></html>"
    }

    private fun style(): String {
        return """
            a {
                font-size: 2em;
                font-weight: bold;
            }

            .odd {
                background: #ffb6c1
            }
            
            .even {
                background: #add8e6
            }
            
            .feed_title {
                font-weight: 700;
                font-size: 25;
            }
            
            .actions {
                width: 30%
            }

            content {
                font-size: 2em;
                width: 70%
            }
        """.trimIndent()
    }

    private fun join(stuff: List<String>): String {
        return stuff.joinToString(separator = "")
    }
}
