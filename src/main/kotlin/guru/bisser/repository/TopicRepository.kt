package guru.bisser.repository

import guru.bisser.config.DatabaseConfig
import guru.bisser.entity.Topic
import java.nio.ByteBuffer
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types
import java.util.UUID

class TopicRepository(
    private val config: DatabaseConfig,
) {
    private fun connect(): Connection = DriverManager.getConnection(config.url, config.user, config.password)

    fun createTableIfNotExists() {
        connect().use { connection ->
            connection.createStatement().use {
                it.execute(
                    """
                    CREATE TABLE IF NOT EXISTS topics (
                        id BINARY(16) PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        parent_id BINARY(16) NULL,
                        order_id INT NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (parent_id) REFERENCES topics (id)
                    )
                    """.trimIndent(),
                )
            }
        }
    }

    fun findAll(): List<Topic> =
        connect().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT id, name, parent_id, order_id FROM topics ORDER BY order_id").use { rs ->
                    buildList {
                        while (rs.next()) add(rs.toTopic())
                    }
                }
            }
        }

    fun nextOrderId(parentId: UUID?): Int =
        connect().use { connection ->
            connection
                .prepareStatement("SELECT COALESCE(MAX(order_id) + 1, 0) FROM topics WHERE parent_id <=> ?")
                .use { statement ->
                    statement.setUuid(1, parentId)
                    statement.executeQuery().use { rs ->
                        rs.next()
                        rs.getInt(1)
                    }
                }
        }

    fun insert(topic: Topic) {
        connect().use { connection ->
            connection
                .prepareStatement("INSERT INTO topics (id, name, parent_id, order_id) VALUES (?, ?, ?, ?)")
                .use { statement ->
                    statement.setUuid(1, topic.id)
                    statement.setString(2, topic.name)
                    statement.setUuid(3, topic.parentId)
                    statement.setInt(4, topic.orderId)
                    statement.executeUpdate()
                }
        }
    }

    private fun PreparedStatement.setUuid(
        index: Int,
        value: UUID?,
    ) {
        if (value == null) setNull(index, Types.BINARY) else setBytes(index, value.toBytes())
    }

    private fun ResultSet.toTopic() =
        Topic(
            id = getBytes("id").toUuid(),
            name = getString("name"),
            parentId = getBytes("parent_id")?.toUuid(),
            orderId = getInt("order_id"),
        )

    private fun UUID.toBytes(): ByteArray =
        ByteBuffer
            .allocate(16)
            .putLong(mostSignificantBits)
            .putLong(leastSignificantBits)
            .array()

    private fun ByteArray.toUuid(): UUID = ByteBuffer.wrap(this).let { UUID(it.long, it.long) }
}
