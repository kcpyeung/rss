package org.net.rss

interface Feed {
    val id: String
    val title: String?
    var items: List<Item>
}
