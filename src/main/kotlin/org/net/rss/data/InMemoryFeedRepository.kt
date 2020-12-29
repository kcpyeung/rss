package org.net.rss.data

import org.net.rss.Feed
import java.io.*


object InMemoryFeedRepository {
    private val feeds: HashMap<String, TimeTrackingRss>

    init {
        if (File("highwatermark.bin").exists()) {
            val fis = FileInputStream("highwatermark.bin")
            val ois = ObjectInputStream(fis)

            @Suppress("UNCHECKED_CAST")
            feeds = ois.readObject() as HashMap<String, TimeTrackingRss>
            ois.close()
        } else {
            feeds = hashMapOf()
        }
    }

    fun add(feed: Feed) = synchronized(this) {
        val stored = feeds[feed.id]
        if (stored == null) {
            feeds[feed.id] = TimeTrackingRss(feed)
        } else {
            val storedRss = stored.feed
            storedRss.addItems(feed.items, stored.lastRefreshedAt)
            feeds[feed.id] = TimeTrackingRss(storedRss)
        }

        save()
    }

    fun hasSource(source: String): Boolean = synchronized(this) {
        return feeds.containsKey(source)
    }

    fun get(source: String): Feed? = synchronized(this) {
        return feeds[source]?.feed
    }

    internal fun clear() {
        feeds.clear()
    }

    fun deleteTo(source: String, guid: String) = synchronized(this) {
        val rss = feeds[source]?.feed ?: return
        rss.deleteTo(guid)
        feeds[source] = TimeTrackingRss(rss)

        save()
    }

    private fun save() {
        val fos = FileOutputStream("highwatermark.bin")
        val oos = ObjectOutputStream(fos)

        oos.writeObject(this.feeds)
        oos.close()
    }
}
