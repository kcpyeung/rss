package org.net.rss.config

data class SectionDto(
  val name: String,
  val subscriptions: List<SubscriptionDto>,
)
