package com.makki.eschool.database

import com.makki.eschool.database.dao.*
import com.makki.eschool.database.dao.auth.HierarchyDao
import com.makki.eschool.database.dao.auth.PersonDao
import com.makki.eschool.database.dao.auth.SecurityDao
import com.makki.eschool.database.dao.auth.SessionDao
import com.makki.eschool.database.dao.movement.MovementDao
import com.makki.eschool.database.dao.schedule.LessonsDao
import com.makki.eschool.database.dao.schedule.PeriodDao
import com.makki.eschool.database.dao.schedule.PersonPeriodDao
import com.makki.eschool.database.dao.schedule.SubjectDao
import kotlinx.io.core.Closeable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseWrapper(val database: Database) : Closeable {

    private var tables = arrayOf(
        PersonDao,
        SessionDao,
        SecurityDao,
        NotificationDao,
        PeriodDao,
        SubjectDao,
        LessonsDao,
        PersonPeriodDao,
        GradesDao,
        MovementDao,
        AssignmentsDao,
        AnnouncementDao,
        HierarchyDao
    )

    fun init() =
        transaction(database) {
            SchemaUtils.drop(*tables)
            SchemaUtils.create(*tables)

            asyncMockData()
        }

    override fun close() {}

}
