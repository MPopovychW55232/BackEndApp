package com.makki.eschool.routing.grades

import com.makki.eschool.database.dao.GradeDbo
import com.makki.eschool.database.dao.GradesDao
import com.makki.eschool.dbQuery
import com.makki.eschool.logger
import com.makki.eschool.routing.auth.AuthManager
import com.makki.eschool.routing.schedule.ScheduleManager

object GradesManager {

    suspend fun getByPeriod(personId: Int, periodId: Int): List<GradeDbo> {
        val person = AuthManager.getUserById(personId)
        val period = ScheduleManager.getPeriodById(periodId)

        return dbQuery(logger) {
            return@dbQuery GradesDao.getByPeriod(person.id, period.id)
        }
    }

    suspend fun addGrade(dbo: GradeDbo) {
        AuthManager.getUserById(dbo.personId)
        ScheduleManager.getSubjectById(dbo.subjectId)

        dbQuery {
            GradesDao.insert(dbo)
        }
    }


}
