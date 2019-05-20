package com.makki.eschool.database.dao.auth

import org.jetbrains.exposed.sql.*

object HierarchyDao : Table("hierarchy_table") {
    val parentId = (integer("parent_id") references PersonDao.id).autoIncrement()
    val studentId = integer("student_id") references PersonDao.id

    fun insert(hierarchy: HierarchyDbo) {
        HierarchyDao.insert {
            it[parentId] = hierarchy.parentId
            it[studentId] = hierarchy.studentId
        }
    }

    fun getLower(lower: Int): HierarchyDbo? {
        return HierarchyDao
            .select { studentId eq lower }
            .firstOrNull()?.build()
    }

    fun getHigher(higher: Int): HierarchyDbo? {
        return HierarchyDao
            .select { parentId eq higher }
            .firstOrNull()?.build()
    }

    fun removeById(hierarchyId: Int) {
        HierarchyDao.deleteWhere { parentId eq hierarchyId }
    }

    fun removeByBulkIds(list: List<Int>) {
        HierarchyDao.deleteWhere { parentId inList list }
    }

    private fun ResultRow.build(): HierarchyDbo {
        return HierarchyDbo(
            get(parentId),
            get(studentId)
        )
    }
}

data class HierarchyDbo(
    val parentId: Int,
    val studentId: Int
)
