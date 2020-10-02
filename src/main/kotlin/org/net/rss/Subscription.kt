package org.net.rss

import java.time.format.DateTimeFormatter

data class Subscription(val url: String, val dateFormat: DateTimeFormatter, val linkRewrite: (String) -> String = { it })
