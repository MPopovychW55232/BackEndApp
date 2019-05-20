package com.makki.eschool.routing.assignments

import com.makki.eschool.base.ApiException.Companion.ReceiverNotFound
import com.makki.eschool.base.ApiException.Companion.SenderNotFound
import com.makki.eschool.database.dao.*
import com.makki.eschool.database.dao.auth.PersonDao
import com.makki.eschool.dbQuery
import com.makki.eschool.logger

object AssignmentsManager {

    suspend fun addAssignment(dbo: AssignmentDbo) =
        dbQuery(logger) {
            PersonDao.getById(dbo.receiverId) ?: throw ReceiverNotFound
            PersonDao.getById(dbo.senderId) ?: throw SenderNotFound
            AssignmentsDao.insert(dbo)
        }

    suspend fun getAssignments(receiverId: Int): List<AssignmentDbo> =
        dbQuery(logger) {
            return@dbQuery AssignmentsDao.getAllForReceiver(receiverId)
        }

    suspend fun getAssignmentById(assignmentId: Int?): AssignmentDbo? =
        dbQuery(logger) {
            if (assignmentId == null) return@dbQuery null
            return@dbQuery AssignmentsDao.getById(assignmentId)
        }

}
