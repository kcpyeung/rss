package org.net.rss.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule

class Subscriptions(yaml: String?) {
    val sections: List<Section>
    val all: List<Subscription>

    init {
        val objectMapper = ObjectMapper(YAMLFactory())
        objectMapper.registerModule(KotlinModule())

        sections = objectMapper
          .readValue(yaml, Array<SectionDto>::class.java)
          .toList()
          .map { Section(it) }

        all = sections.flatMap { it.subscriptions }
    }
}
