package com.makki.eschool.database.dao

import com.makki.eschool.database.dao.auth.PersonDao
import org.jetbrains.exposed.sql.*

object AssignmentsDao : Table("assignments_table") {

    val id = integer("id").primaryKey().autoIncrement()
    val receiverId = (integer("receiver_id") references PersonDao.id)
    val senderId = (integer("sender_id") references PersonDao.id)
    val senderDisplayName = varchar("sender_display_name", 30)
    val title = text("title")
    val message = text("message")
    val timestamp = long("timestamp")

    fun getById(assignmentId: Int): AssignmentDbo? {
        return AssignmentsDao
            .select { id eq assignmentId }
            .firstOrNull()?.build() ?: return null
    }

    fun getAllForReceiver(id: Int): List<AssignmentDbo> {
        return AssignmentsDao.select { receiverId eq id }
            .map { it.build() }
    }

    fun insert(dbo: AssignmentDbo) {
        AssignmentsDao.insert {
            it[receiverId] = dbo.receiverId
            it[senderId] = dbo.senderId
            it[senderDisplayName] = dbo.senderDisplayName
            it[title] = dbo.title
            it[message] = dbo.message
            it[timestamp] = dbo.timestamp
        }
    }

    fun update(dbo: AssignmentDbo) {
        AssignmentsDao.update({ id eq dbo.id}) {
            it[receiverId] = dbo.receiverId
            it[senderId] = dbo.senderId
            it[senderDisplayName] = dbo.senderDisplayName
            it[title] = dbo.title
            it[message] = dbo.message
            it[timestamp] = dbo.timestamp
        }
    }

    fun removeById(assignmentId: Int) {
        AssignmentsDao.deleteWhere { id eq assignmentId }
    }

    fun removeByBulkIds(list: List<Int>) {
        AssignmentsDao.deleteWhere { id inList list }
    }

    private fun ResultRow.build(): AssignmentDbo {
        return AssignmentDbo(
            get(id),
            get(receiverId),
            get(senderId),
            get(senderDisplayName),
            get(title),
            get(message),
            get(timestamp)
        )
    }
}

data class AssignmentDbo(
    val id: Int,
    val receiverId: Int,
    val senderId: Int,
    val senderDisplayName: String,
    val title: String,
    val message: String,
    val timestamp: Long
)
