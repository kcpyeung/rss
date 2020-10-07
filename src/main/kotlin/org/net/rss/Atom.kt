package org.net.rss

import org.net.rss.config.Subscription
import org.net.rss.xml.XmlHelper

class Atom(atom: String, subscription: Subscription) {
    val id = subscription.feedIdGen(subscription.url)
    val title: String?
    val items: List<Item>

    init {
        val xmlHelper = XmlHelper(atom)

        title = xmlHelper.getAsString("/feed/title")
        items = xmlHelper
          .getAtomLookups("/feed/entry")
          .map { Item(it, subscription) }
    }
}
