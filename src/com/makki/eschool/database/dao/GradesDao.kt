package com.makki.eschool.database.dao

import com.makki.eschool.database.dao.auth.PersonDao
import com.makki.eschool.database.dao.schedule.PeriodDao
import com.makki.eschool.database.dao.schedule.SubjectDao
import org.jetbrains.exposed.sql.*

object GradesDao : Table("grades_table") {

    val id = integer("id").primaryKey().autoIncrement()
    val personId = (integer("person_id") references PersonDao.id)
    val periodId = (integer("period_id") references PeriodDao.id)
    val subjectId = (integer("subject_id") references SubjectDao.id)
    val timestamp = long("timestamp")
    val grade = float("grade")
    val notices = text("notices")

    fun getById(grade: Int): GradeDbo? {
        return GradesDao
            .select { id eq grade }
            .firstOrNull()?.build()
    }

    fun getByPeriod(person: Int, period: Int): List<GradeDbo> {
        return GradesDao
            .select { (periodId eq period) and (personId eq person) }
            .map { it.build() }
    }

    fun getBySubject(person: Int, subject: Int): List<GradeDbo> {
        return GradesDao
            .select { (subjectId eq subject) and (personId eq person) }
            .map { it.build() }
    }

    fun insert(dbo: GradeDbo) {
        GradesDao.insert {
            it[personId] = dbo.personId
            it[periodId] = dbo.periodId
            it[subjectId] = dbo.subjectId
            it[timestamp] = dbo.timestamp
            it[grade] = dbo.grade
            it[notices] = dbo.notices
        }
    }

    private fun ResultRow.build(): GradeDbo {
        return GradeDbo(
            get(id),
            get(personId),
            get(periodId),
            get(subjectId),
            get(timestamp),
            get(grade),
            get(notices)
        )
    }

}

data class GradeDbo(
    val id: Int,
    val personId: Int,
    val periodId: Int,
    val subjectId: Int,
    val timestamp: Long,
    val grade: Float,
    val notices: String
)
