package org.net.rss.html

import org.net.rss.Rss

class Div(rss: Rss) {
    val id = rss.title?.toLowerCase()?.replace(" ", "_")

}
