package org.net.rss

import java.time.format.DateTimeFormatter

data class SubscriptionDto(
  val url: String,
  val dateFormat: DateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME,
  val linkRewriteFactoryClass: String?) {

    fun linkRewriter(): (String) -> String {
        if (linkRewriteFactoryClass == null) {
            return { it }
        } else {
            @Suppress("UNCHECKED_CAST")
            val clazz = Class.forName(linkRewriteFactoryClass) as Class<LinkRewriterFactory>
            val factory = clazz.getDeclaredConstructor().newInstance()
            return factory.get()
        }
    }
}
