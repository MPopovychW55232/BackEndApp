package com.makki.eschool.database.dao

import com.makki.eschool.database.dao.auth.PersonDao
import org.jetbrains.exposed.sql.*

object AnnouncementDao: Table("announcement_table") {

    val id = integer("id").primaryKey().autoIncrement()
    val senderId = (integer("sender_id") references PersonDao.id)
    val senderDisplayName = varchar("sender_display_name", 30)
    val title = text("title")
    val message = text("message")
    val timestamp = long("timestamp")


    fun getAll(): List<AnnouncementDbo> {
        return AnnouncementDao.selectAll().map { it.build() }
    }

    fun insert(dbo: AnnouncementDbo) {
        AnnouncementDao.insert {
            it[senderId] = dbo.senderId
            it[senderDisplayName] = dbo.senderDisplayName
            it[title] = dbo.title
            it[message] = dbo.message
            it[timestamp] = dbo.timestamp
        }
    }

    fun update(dbo: AnnouncementDbo) {
        AnnouncementDao.update({ id eq dbo.id}) {
            it[senderId] = dbo.senderId
            it[senderDisplayName] = dbo.senderDisplayName
            it[title] = dbo.title
            it[message] = dbo.message
            it[timestamp] = dbo.timestamp
        }
    }

    fun removeById(assignmentId: Int) {
        AnnouncementDao.deleteWhere { id eq assignmentId }
    }

    private fun ResultRow.build(): AnnouncementDbo {
        return AnnouncementDbo(
            get(id),
            get(senderId),
            get(senderDisplayName),
            get(title),
            get(message),
            get(timestamp)
        )
    }
}

data class AnnouncementDbo(
    val id: Int,
    val senderId: Int,
    val senderDisplayName: String,
    val title: String,
    val message: String,
    val timestamp: Long
)
