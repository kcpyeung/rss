package org.net.rss

import org.net.rss.config.Subscription
import org.net.rss.xml.XmlHelper

class Rss(rss: String, subscription: Subscription) : Feed {
    override val id = subscription.feedIdGen(subscription.url)
    override val title: String?
    override lateinit var items: List<Item>

    init {
        val xmlHelper = XmlHelper(rss)

        title = subscription.title ?: xmlHelper.getAsString("/rss/channel/title")
        safeCopy(xmlHelper
          .getRssLookups("/rss/channel/item")
          .map { Item(it, subscription) })
    }
}
