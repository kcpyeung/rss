package org.net.rss

import org.net.rss.config.Subscription
import org.net.rss.xml.XmlHelper

class Atom(atom: String, subscription: Subscription) : Feed {
    override val id = subscription.feedIdGen(subscription.url)
    override val title: String?
    override lateinit var items: List<Item>

    init {
        val xmlHelper = XmlHelper(atom)

        title = xmlHelper.getAsString("/feed/title")
        items = xmlHelper
          .getAtomLookups("/feed/entry")
          .map { Item(it, subscription) }
    }
}
