package com.makki.eschool.database.dao.schedule

import com.makki.eschool.database.dao.auth.PersonDao
import org.jetbrains.exposed.sql.*

object PersonPeriodDao : Table("period_relation_table") {

    val id = integer("id").primaryKey().autoIncrement()
    val personId = (integer("person_id") references PersonDao.id)
    val periodId = (integer("period_id") references PeriodDao.id)

    fun insert(period: PersonPeriodDbo) {
        PersonPeriodDao.insert {
            it[personId] = period.personId
            it[periodId] = period.periodId
        }
    }

    fun getPeriodById(ownId: Int): PersonPeriodDbo? {
        return PersonPeriodDao
            .select { id eq ownId }
            .firstOrNull()?.build()
    }

    fun getPeriodByPair(person: Int, period: Int): PersonPeriodDbo? {
        return PersonPeriodDao
            .select { (personId eq person) and (periodId eq period) }
            .firstOrNull()?.build()
    }

    fun getAllPersons(period: Int): List<PersonPeriodDbo> {
        return PersonPeriodDao
            .select { periodId eq period }
            .map { it.build() }
    }

    fun getAllPeriods(person: Int): List<PersonPeriodDbo> {
        return PersonPeriodDao
            .select { personId eq person }
            .map { it.build() }
    }

    private fun ResultRow.build(): PersonPeriodDbo {
        return PersonPeriodDbo(
            get(id),
            get(personId),
            get(periodId)
        )
    }

}

data class PersonPeriodDbo(
    val id: Int,
    val personId: Int,
    val periodId: Int
)