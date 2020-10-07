package org.net.rss.config

class OldReddit : LinkRewriterFactory {
    override fun get(): (String) -> String {
        return { it.replace("www.reddit.com", "old.reddit.com") }
    }
}