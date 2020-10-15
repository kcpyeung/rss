package org.net.rss.config

class Section(dto: SectionDto) {
    val name = dto.name
    val subscriptions = dto.subscriptions.map { Subscription(it) }

    fun hasUnreadItems(): Boolean {
        return subscriptions
          .map { it.hasUnreadItems() }
          .fold(false) { anyUnread, thisUnread -> anyUnread || thisUnread }
    }
}
