package org.net.rss.config

data class SubscriptionDto(
  val url: String,
  val dateFormat: String?,
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
