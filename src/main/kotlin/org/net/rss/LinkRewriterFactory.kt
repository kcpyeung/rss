package org.net.rss

interface LinkRewriterFactory {
    fun get(): (String) -> String
}
