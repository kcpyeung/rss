package org.net.rss.config

interface LinkRewriterFactory {
    fun get(): (String) -> String
}
