package com.makki.eschool.routing.movement

import com.makki.eschool.database.dao.movement.MovementDao
import com.makki.eschool.database.dao.movement.MovementDbo
import com.makki.eschool.dbQuery
import com.makki.eschool.logger
import com.makki.eschool.routing.auth.AuthManager

object MovementManager {

    suspend fun addRecords(list: List<MovementDbo>) =
        dbQuery(logger) {
            MovementDao.insert(list)
        }

    suspend fun getRecordsByMonth(personId: Int, monthIndex: Int): List<MovementDbo> {
        val person = AuthManager.getUserById(personId)
        return dbQuery(logger) {
            MovementDao.getByPersonAndMonth(person.id, monthIndex)
        }
    }

}