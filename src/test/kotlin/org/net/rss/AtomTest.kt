package org.net.rss

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.net.rss.config.Subscription
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class AtomTest {
    val subscriptionWithTitle = Subscription("https://theconversation.com/au/articles.atom", dateFormat = DateTimeFormatter.ISO_DATE_TIME, title = "Overriding title")
    val subscriptionWithoutTitle = Subscription("https://theconversation.com/au/articles.atom", dateFormat = DateTimeFormatter.ISO_DATE_TIME)

    val atomString = """
|<feed xml:lang="en-US" xmlns="http://www.w3.org/2005/Atom" xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
|   <id>tag:theconversation.com,2011:/au/articles</id>
|   <link rel="alternate" type="text/html" href="https://theconversation.com"/>
|   <link rel="self" type="application/atom+xml" href="https://theconversation.com/au/articles.atom"/>
|   <title>The Conversation – Articles (AU)</title>
|   <updated>2020-10-06T05:45:51Z</updated>
|  <entry>
|    <id>tag:theconversation.com,2011:article/147557</id>
|    <published>2020-10-06T05:45:51Z</published>
|    <updated>2020-10-06T05:45:51Z</updated>
|    <link rel="alternate" type="text/html" href="https://theconversation.com/the-budget-assumes-a-covid-19-vaccine-becomes-available-next-year-is-this-feasible-147557"/>
|    <title>The budget assumes a COVID-19 vaccine becomes available next year. Is this feasible?</title>
|    <content type="html">Some content text</content>
|    <summary>A group of 28 vaccine researchers said we might have a vaccine by late-2021, though it could take until well into 2022.</summary>
|    <author>
|      <name>Holly Seale, Senior Lecturer, UNSW</name>
|      <foaf:homepage rdf:resource="https://theconversation.com/profiles/holly-seale-94294"/>
|    </author>
|    <rights>Licensed as Creative Commons – attribution, no derivatives.</rights>
|  </entry>
|  <entry>
|    <id>tag:theconversation.com,2011:article/147351</id>
|    <published>2020-10-06T05:28:06Z</published>
|    <updated>2020-10-06T05:28:06Z</updated>
|    <link rel="alternate" type="text/html" href="https://theconversation.com/netflixs-the-social-dilemma-highlights-the-problem-with-social-media-but-whats-the-solution-147351"/>
|    <title>Netflix's The Social Dilemma highlights the problem with social media, but what's the solution?</title>
|    <content type="html">Some other content text</content>
|    <author>
|      <name>Belinda Barnet, Senior Lecturer in Media and Communications, Swinburne University of Technology</name>
|      <foaf:homepage rdf:resource="https://theconversation.com/profiles/belinda-barnet-219971"/>
|    </author>
|    <author>
|      <name>Diana Bossio, Lecturer, Media and Communications, Swinburne University of Technology</name>
|      <foaf:homepage rdf:resource="https://theconversation.com/profiles/diana-bossio-1649"/>
|    </author>
|    <rights>Licensed as Creative Commons – attribution, no derivatives.</rights>
|  </entry>
|  <entry>
|    <id>tag:theconversation.com,2011:article/147194</id>
|    <published>2020-10-06T04:26:54Z</published>
|    <updated>2020-10-06T04:26:54Z</updated>
|    <link rel="alternate" type="text/html" href="https://theconversation.com/unis-are-run-like-corporations-but-their-leaders-are-less-accountable-heres-an-easy-way-to-fix-that-147194"/>
|    <title>Unis are run like corporations but their leaders are less accountable. Here's an easy way to fix that</title>
|    <summary>Ironically, a bit more of the right kind of corporatisation might help remedy the worst aspects of the current model of corporatised universities.</summary>
|    <author>
|      <name>Luke Beck, Associate Professor of Constitutional Law, Monash University</name>
|      <foaf:homepage rdf:resource="https://theconversation.com/profiles/luke-beck-184936"/>
|    </author>
|    <rights>Licensed as Creative Commons – attribution, no derivatives.</rights>
|  </entry>
|</feed>
    """.trimMargin()

    @Test
    fun `atom has title`() {
        assertThat(Atom(atomString, subscriptionWithoutTitle).title, `is`("The Conversation – Articles (AU)"))
    }

    @Test
    fun `title from atom can be overridden in subscription`() {
        assertThat(Atom(atomString, subscriptionWithTitle).title, `is`("Overriding title"))
    }

    @Test
    fun `atom id is base64-sha1 of its subscription url`() {
        assertThat(Atom(atomString, subscriptionWithoutTitle).id, `is`("OIS3yBgpiekmgMj8jwAxBqkhM7o="))
    }

    @Nested
    inner class AtomEntryMappedToItem {
        private val atom = Atom(atomString, subscriptionWithoutTitle)

        @Test
        fun `atom entry is item`() {
            assertThat(atom.items.size, `is`(3))

            assertThat(atom.items[0].title, `is`("The budget assumes a COVID-19 vaccine becomes available next year. Is this feasible?"))
            assertThat(atom.items[1].title, `is`("Netflix's The Social Dilemma highlights the problem with social media, but what's the solution?"))
        }

        @Test
        fun `item description is mapped to summary`() {
            assertThat(atom.items[0].description, `is`("A group of 28 vaccine researchers said we might have a vaccine by late-2021, though it could take until well into 2022."))
            assertThat(atom.items[1].description, `is`(nullValue()))
            assertThat(atom.items[2].description, `is`("Ironically, a bit more of the right kind of corporatisation might help remedy the worst aspects of the current model of corporatised universities."))
        }

        @Test
        fun `item content is mapped to content`() {
            assertThat(atom.items[0].content, `is`("Some content text"))
            assertThat(atom.items[1].content, `is`("Some other content text"))
            assertThat(atom.items[2].content, `is`(nullValue()))
        }

        @Test
        fun `atom updated is mapped to pubDate`() {
            assertThat(atom.items[0].pubDate, `is`(ZonedDateTime.ofInstant(Instant.parse("2020-10-06T05:45:51Z"), ZoneId.of("Z"))))
            assertThat(atom.items[1].pubDate, `is`(ZonedDateTime.ofInstant(Instant.parse("2020-10-06T05:28:06Z"), ZoneId.of("Z"))))
        }

        @Test
        fun `atom link-href is mapped to link`() {
            assertThat(atom.items[0].link, `is`("https://theconversation.com/the-budget-assumes-a-covid-19-vaccine-becomes-available-next-year-is-this-feasible-147557"))
            assertThat(atom.items[1].link, `is`("https://theconversation.com/netflixs-the-social-dilemma-highlights-the-problem-with-social-media-but-whats-the-solution-147351"))
        }
    }
}
