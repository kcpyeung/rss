package org.net.rss

import org.net.rss.util.Id
import java.time.format.DateTimeFormatter

data class Subscription(val url: String,
                        val dateFormat: DateTimeFormatter,
                        val linkRewrite: (String) -> String = { it },
                        val idGenerator: (String) -> String = { Id(it).hash })
