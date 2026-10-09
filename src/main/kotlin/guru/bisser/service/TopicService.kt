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

    // A topic moved to another parent goes last among its new siblings.
    fun update(
        id: UUID,
        name: String,
        parentId: UUID?,
    ): Topic {
        require(name.isNotBlank()) { "Topic name must not be blank" }
        val topics = topicRepository.findAll()
        val current = requireNotNull(topics.find { it.id == id }) { "Topic $id not found" }
        require(parentId == null || allowedParents(current, topics).any { it.id == parentId }) {
            "A topic cannot be moved under itself or its descendants"
        }
        val orderId = if (parentId == current.parentId) current.orderId else topicRepository.nextOrderId(parentId)
        val updated = current.copy(name = name.trim(), parentId = parentId, orderId = orderId)
        topicRepository.update(updated)
        return updated
    }

    // Deletes the topic together with all its descendants and returns their ids.
    fun delete(id: UUID): Set<UUID> {
        val topics = topicRepository.findAll()
        val topic = requireNotNull(topics.find { it.id == id }) { "Topic $id not found" }
        val ids = subtree(topic, topics).map { it.id }
        topicRepository.deleteAll(ids)
        return ids.toSet()
    }

    // Every topic except `topic` and its descendants, so a move can't create a cycle.
    fun allowedParents(
        topic: Topic,
        topics: List<Topic>,
    ): List<Topic> {
        val excluded = subtree(topic, topics).map { it.id }.toSet()
        return topics.filter { it.id !in excluded }
    }

    // `topic` and all its descendants, children before their parents.
    fun subtree(
        topic: Topic,
        topics: List<Topic>,
    ): List<Topic> {
        val childrenOf = topics.groupBy { it.parentId }

        fun collect(t: Topic): List<Topic> = childrenOf[t.id].orEmpty().flatMap { collect(it) } + t

        return collect(topic)
    }
}
