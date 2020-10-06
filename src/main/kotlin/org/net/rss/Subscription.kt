package org.net.rss

import org.net.rss.util.Id
import java.time.format.DateTimeFormatter

data class Subscription(val url: String,
                        val dateFormat: DateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME,
                        val linkRewrite: (String) -> String = { it },
                        val feedIdGen: (String) -> String = { Id(it).hash },
                        val itemIdGen: (String) -> String = { Id(it).hash }) {
    constructor(dto: SubscriptionDto): this(dto.url,
      linkRewrite = dto.linkRewriter())
}
