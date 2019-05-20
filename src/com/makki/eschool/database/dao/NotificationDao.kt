package com.makki.eschool.database.dao

import com.makki.eschool.database.dao.auth.PersonDao
import org.jetbrains.exposed.sql.*

object NotificationDao : Table("notification_table") {

    val id = integer("id").primaryKey().autoIncrement()
    val receiverId = (integer("receiver_id") references PersonDao.id)
    val senderId = (integer("sender_id") references PersonDao.id)
    val senderDisplayName = varchar("sender_display_name", 30)
    val title = text("title")
    val message = text("message")
    val fresh = bool("fresh")
    val timestamp = long("timestamp")

    fun getById(notificationId: Int): NotificationDbo? {
        return NotificationDao
            .select { id eq notificationId }
            .firstOrNull()?.build() ?: return null
    }

    fun getAllForReceiver(id: Int): List<NotificationDbo> {
        return NotificationDao.select { receiverId eq id }
            .map { it.build() }
    }

    fun insert(dbo: NotificationDbo) {
        NotificationDao.insert {
            it[receiverId] = dbo.receiverId
            it[senderId] = dbo.senderId
            it[senderDisplayName] = dbo.senderDisplayName
            it[title] = dbo.title
            it[message] = dbo.message
            it[fresh] = dbo.fresh
            it[timestamp] = dbo.timestamp
        }
    }

    fun removeById(notificationId: Int) {
        NotificationDao.deleteWhere { id eq notificationId }
    }

    fun markAsRead(notificationId: Int) {
        NotificationDao.update({id eq notificationId}) {
            it[fresh] = false
        }
    }

    private fun ResultRow.build(): NotificationDbo {
        return NotificationDbo(
            get(id),
            get(receiverId),
            get(senderId),
            get(senderDisplayName),
            get(title),
            get(message),
            get(fresh),
            get(timestamp)
        )
    }
}

data class NotificationDbo(
    val id: Int,
    val receiverId: Int,
    val senderId: Int,
    val senderDisplayName: String,
    val title: String,
    val message: String,
    val fresh: Boolean = true,
    val timestamp: Long
)
