package org.net.rss.config

class QueryRemover : LinkRewriterFactory {
    override fun get(): (String) -> String {
        return { it.substringBefore("?") }
    }
}