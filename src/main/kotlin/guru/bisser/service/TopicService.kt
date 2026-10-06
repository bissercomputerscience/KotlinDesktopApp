package guru.bisser.service

import guru.bisser.entity.Topic
import guru.bisser.repository.TopicRepository
import java.util.UUID

class TopicService(
    private val topicRepository: TopicRepository,
) {
    fun findAll(): List<Topic> = topicRepository.findAll()

    // A new topic goes last among its siblings.
    fun create(
        name: String,
        parentId: UUID? = null,
    ): Topic {
        require(name.isNotBlank()) { "Topic name must not be blank" }
        val topic = Topic(UUID.randomUUID(), name.trim(), parentId, topicRepository.nextOrderId(parentId))
        topicRepository.insert(topic)
        return topic
    }
}
