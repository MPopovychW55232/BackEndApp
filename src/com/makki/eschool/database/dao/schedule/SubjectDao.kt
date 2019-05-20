package com.makki.eschool.database.dao.schedule

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object SubjectDao : Table("subject_table") {

    val id = integer("id").primaryKey().autoIncrement()
    val periodId = (integer("period_id") references PeriodDao.id)
    val name = varchar("name", 60)
    val description = text("description")

    fun getById(subject: Int): SubjectDbo? {
        return SubjectDao
            .select { id eq subject }
            .firstOrNull()?.build()
    }

    fun getByPeriod(period: Int): List<SubjectDbo> {
        return SubjectDao
            .select { periodId eq period }
            .map { it.build() }
    }

    fun insert(lesson: SubjectDbo) {
        SubjectDao.insert {
            it[periodId] = lesson.periodId
            it[name] = lesson.name
            it[description] = lesson.description
        }
    }

    private fun ResultRow.build(): SubjectDbo {
        return SubjectDbo(
            get(id),
            get(periodId),
            get(name),
            get(description)
        )
    }

}

data class SubjectDbo(
    val id: Int,
    val periodId: Int,
    val name: String,
    val description: String
)