package com.makki.eschool.database.dao.schedule

import org.jetbrains.exposed.sql.*

object PeriodDao : Table("period_table") {

    val id = integer("id").primaryKey().autoIncrement()
    var startTime = long("start_time")
    var endTime = long("end_time")
    val name = varchar("name", 60).primaryKey()
    val description = text("description")
    val modified = long("modify_timestamp")

    fun getById(period: Int): PeriodDbo? {
        return PeriodDao
            .select { id eq period }
            .firstOrNull()?.build()
    }

    fun getByName(periodName: String): PeriodDbo? {
        return PeriodDao
            .select { name eq periodName }
            .firstOrNull()?.build()
    }

    fun getByIds(ids: List<Int>): List<PeriodDbo> {
        if (ids.isEmpty()) return emptyList()

        return PeriodDao
            .select { id inList ids }
            .map { it.build() }
    }

    fun insert(period: PeriodDbo) {
        PeriodDao.insert {
            it[startTime] = period.startTime
            it[endTime] = period.endTime
            it[name] = period.name
            it[description] = period.description
            it[modified] = System.currentTimeMillis()
        }
    }

    fun update(period: PeriodDbo) {
        PeriodDao.update({id eq period.id}) {
            it[startTime] = period.startTime
            it[endTime] = period.endTime
            it[name] = period.name
            it[description] = period.description
            it[modified] = System.currentTimeMillis()
        }
    }

    fun setAsModified(period: Int) {
        PeriodDao.update({id eq period}) {
            it[modified] = System.currentTimeMillis()
        }
    }

    private fun ResultRow.build(): PeriodDbo {
        return PeriodDbo(
            get(id),
            get(startTime),
            get(endTime),
            get(name),
            get(description),
            get(modified)
        )
    }

}

data class PeriodDbo(
    val id: Int,
    val startTime: Long,
    val endTime: Long,
    val name: String,
    val description: String,
    val modified: Long = 0
)