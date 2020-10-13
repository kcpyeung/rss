package org.net.rss.config

class Section(dto: SectionDto) {
    val name = dto.name
    val subscriptions = dto.subscriptions.map { Subscription(it) }
}
