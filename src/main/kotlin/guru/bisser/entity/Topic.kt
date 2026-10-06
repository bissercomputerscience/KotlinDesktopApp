package guru.bisser.entity

import java.util.UUID

data class Topic(
    val id: UUID,
    val name: String,
    val parentId: UUID?,
    val orderId: Int,
)
