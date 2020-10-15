package org.net.rss.html

import org.net.rss.config.Section
import org.net.rss.config.Subscriptions

class HomePage(private val subscriptions: Subscriptions) {

    fun links(): List<String> {
        return subscriptions.sections.map { linkOrText(it) }
    }

    private fun linkOrText(section: Section): String {
        if (section.hasUnreadItems()) {
            return "<a href=\"/${section.name}\">${section.name}</a>"
        } else {
            return section.name
        }
    }

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
        return links().joinToString(separator = "") { "<tr><td>${it}</td></tr>" }
    }
}
