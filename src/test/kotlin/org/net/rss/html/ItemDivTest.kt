package org.net.rss.html

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.net.rss.Item

class ItemDivTest {
    @Test
    fun `even indices have even class`() {
        val itemDiv = ItemDiv(0, item(), feedDiv())

        assertThat(itemDiv.asHtml(), startsWith("""<div class="even" id="helloworld-0">"""))
    }

    @Test
    fun `odd indices have odd class`() {
        val itemDiv = ItemDiv(1, item(), feedDiv())

        assertThat(itemDiv.asHtml(), startsWith("""<div class="odd" id="helloworld-1">"""))
    }

    private fun item(): Item {
        return mockk<Item>(relaxed = true)
    }

    private fun feedDiv(): FeedDiv {
        val div = mockk<FeedDiv>()
        every { div.id } returns "helloworld"

        return div
    }
}
