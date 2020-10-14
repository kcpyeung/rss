package org.net.rss.html

import org.net.rss.config.Subscriptions

class HomePage(subscriptions: Subscriptions) {

    val links = subscriptions.sections.map { "<a href=\"/${it.name}\">${it.name}</a>" }

    fun asHtml(): String {
        return """|
            |<html>
            |<body>
            |   <table>
            |       <tbody>
            |       ${linkRows()}
            |       </tbody>
            |   </table>
            |</body>
            |</html>
        """.trimMargin()
    }

    private fun linkRows(): String {
        return links.joinToString(separator = "") { "<tr><td>${it}</td></tr>" }
    }
}
