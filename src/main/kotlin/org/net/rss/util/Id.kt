package org.net.rss.util

import java.security.MessageDigest
import java.util.*

class Id(id: String) {
    val hash = sha1(id)

    private fun sha1(url: String): String {
        val digest = MessageDigest.getInstance("SHA-1").digest(url.toByteArray())

        return Base64.getUrlEncoder().encodeToString(digest)
    }
}
