package org.net.rss

class QueryRemover : LinkRewriterFactory {
    override fun get(): (String) -> String {
        return { it.substringBefore("?") }
    }
}