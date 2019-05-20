package com.makki.eschool.database.dao.auth

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object SessionDao : Table("session_table") {

    val id = (integer("id") references PersonDao.id).primaryKey()
    val key = varchar("key", 100)

    fun getById(personId: Int): SessionDbo? {
        return SessionDao
            .select { id eq personId }
            .firstOrNull()?.build() ?: return null
    }

    fun getBySid(sid: String): SessionDbo? {
        return SessionDao
            .select { key eq sid }
            .firstOrNull()?.build() ?: return null
    }

    fun insert(session: SessionDbo) {
        SessionDao.insert {
            it[id] = session.id
            it[key] = session.sid
        }
    }

    private fun ResultRow.build(): SessionDbo {
        return SessionDbo(
            get(id),
            get(key)
        )
    }
}

data class SessionDbo(val id: Int, val sid: String)

