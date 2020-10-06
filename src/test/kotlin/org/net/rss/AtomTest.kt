package org.net.rss

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.net.rss.config.Subscription
import java.time.format.DateTimeFormatter

class AtomTest {
    val subscription = Subscription("https://theconversation.com/au/articles.atom", DateTimeFormatter.ISO_DATE_TIME)

    val atom = """
|<feed xml:lang="en-US" xmlns="http://www.w3.org/2005/Atom" xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
|   <id>tag:theconversation.com,2011:/au/articles</id>
|   <link rel="alternate" type="text/html" href="https://theconversation.com"/>
|   <link rel="self" type="application/atom+xml" href="https://theconversation.com/au/articles.atom"/>
|   <title>The Conversation – Articles (AU)</title>
|   <updated>2020-10-06T05:45:51Z</updated>
|   <entry>
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
|    <summary>The documentary educates viewers about the problems social networks present to both our privacy and agency online. But it doesn't really tell us how to fight the tide.</summary>
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
|</feed>
    """.trimMargin()

    @Test
    fun `atom has title`() {
        assertThat(Atom(atom, subscription).title, `is`("The Conversation – Articles (AU)"))
    }

    @Test
    fun `atom id is base64-sha1 of its subscription url`() {
        assertThat(Atom(atom, subscription).id, `is`("OIS3yBgpiekmgMj8jwAxBqkhM7o="))
    }
}
