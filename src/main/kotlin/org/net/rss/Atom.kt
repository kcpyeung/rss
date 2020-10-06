package org.net.rss

import org.net.rss.config.Subscription
import org.net.rss.xml.XmlHelper

class Atom(atom: String, subscription: Subscription) {
    val id = subscription.feedIdGen(subscription.url)
    val title: String?

    init {
        val xmlHelper = XmlHelper(atom)

        title = xmlHelper.getAsString("/feed/title")
    }
}
