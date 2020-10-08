package org.net.rss.html

class Page(private val feedDivs: List<FeedDiv>) {
    private val divs = join(feedDivs.map { it.asHtml() })

    private fun toc(): String {
        val feedLinks =
          join(feedDivs.map { """<div><b>${titleOrLink(it)}</b></div><p/>""" })

        return """
            <div id="toc">
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
            
            .title_link {
                font-weight: 700;
            }
            
            .actions {
                width: 30%
            }

            .content {
                width: 70%
            }
        """.trimIndent()
    }

    private fun join(stuff: List<String>): String {
        return stuff.joinToString(separator = "")
    }
}