package com.makki.eschool.database.dao.movement

import com.makki.eschool.database.dao.schedule.PeriodDao
import org.jetbrains.exposed.sql.*

object MovementDao: Table("movement_table") {

    val id = integer("id").primaryKey().autoIncrement()
    var personId = (integer("person_id") references PeriodDao.id)
    var gate = varchar("gate", 60)
    var monthId = integer("month_id")
    var dayId = integer("day_id")
    val timestamp = long("timestamp")
    val entered = bool("entered")

    fun getByPersonAndMonth(person: Int, monthIndex: Int): List<MovementDbo> {
        return MovementDao
            .select { (personId eq person) and (monthId eq monthIndex) }
            .map { it.build() }
    }

    fun insert(dbo: MovementDbo) {
        MovementDao.insert {
            it[personId] = dbo.personId
            it[gate] = dbo.gate
            it[monthId] = dbo.monthId
            it[dayId] = dbo.dayId
            it[timestamp] = dbo.timestamp
            it[entered] = dbo.entered
        }
    }

    fun insert(list: List<MovementDbo>) {
        MovementDao.batchInsert(list) { dbo ->
            this[personId] = dbo.personId
            this[gate] = dbo.gate
            this[monthId] = dbo.monthId
            this[dayId] = dbo.dayId
            this[timestamp] = dbo.timestamp
            this[entered] = dbo.entered
        }
    }

    private fun ResultRow.build(): MovementDbo {
        return MovementDbo(
            get(id),
            get(personId),
            get(gate),
            get(monthId),
            get(dayId),
            get(timestamp),
            get(entered)
        )
    }
}

data class MovementDbo(
    val id: Int,
    val personId: Int,
    val gate: String,
    val monthId: Int,
    val dayId: Int,
    val timestamp: Long,
    val entered: Boolean
)
