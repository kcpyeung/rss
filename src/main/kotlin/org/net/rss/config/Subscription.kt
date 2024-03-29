package org.net.rss.config

import org.net.rss.data.InMemoryFeedRepository
import org.net.rss.util.Id
import java.time.format.DateTimeFormatter

data class Subscription(val url: String,
                        val dateFormat: DateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME,
                        val linkRewrite: (String) -> String = { it },
                        val feedIdGen: (String) -> String = { Id(it).hash },
                        val itemIdGen: (String) -> String = { Id(it).hash },
                        val title: String? = null) {
    constructor(dto: SubscriptionDto) : this(dto.url,
      linkRewrite = dto.linkRewriter(),
      dateFormat = if (dto.dateFormat == null) DateTimeFormatter.RFC_1123_DATE_TIME else DateTimeFormatter.ofPattern(dto.dateFormat),
      title = dto.title,
    )

    fun hasUnreadItems(): Boolean {
        val empty = InMemoryFeedRepository
          .get(feedIdGen(url))
          ?.items
          ?.isEmpty() ?: true

        return !empty
    }
}
