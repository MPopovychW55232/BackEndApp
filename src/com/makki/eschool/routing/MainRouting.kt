package com.makki.eschool.routing

import com.makki.eschool.base.ApiException.Companion.InvalidMethod
import com.makki.eschool.base.ApiException.Companion.ReceiverNotFound
import com.makki.eschool.base.ApiException.Companion.SessionInvalid
import com.makki.eschool.base.ApiException.Companion.SessionMissing
import com.makki.eschool.base.ApiException.Companion.UserNotFound
import com.makki.eschool.base.PermissionHelper
import com.makki.eschool.base.PermissionLevel
import com.makki.eschool.database.dao.auth.HierarchyDao
import com.makki.eschool.database.dao.auth.PersonDao
import com.makki.eschool.database.dao.auth.PersonDbo
import com.makki.eschool.database.dao.auth.SessionDao
import com.makki.eschool.dbQuery
import com.makki.eschool.logger
import com.makki.eschool.routing.announcements.applyAnnouncementsRouting
import com.makki.eschool.routing.assignments.applyAssignmentsRouting
import com.makki.eschool.routing.auth.AuthManager
import com.makki.eschool.routing.auth.applyAuthRouting
import com.makki.eschool.routing.grades.applyGradeRouting
import com.makki.eschool.routing.movement.applyMovementRouting
import com.makki.eschool.routing.notifications.applyNotificationRouting
import com.makki.eschool.routing.schedule.applyScheduleRouting
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing


fun Application.applyRouting() {
    routing {
        route("/api") {
            applyAuthRouting()
            applyNotificationRouting()
            applyScheduleRouting()
            applyGradeRouting()
            applyMovementRouting()
            applyAssignmentsRouting()
            applyAnnouncementsRouting()
        }

        get("/{...}") { throw InvalidMethod }
        post("/{...}") { throw InvalidMethod }
    }
}

suspend fun ApplicationCall.verifySession(): PersonDbo =
    dbQuery(logger) {
        val sid = request.queryParameters["sid"] ?: throw SessionMissing

        PersonDao.getById(SessionDao.getBySid(sid)?.id ?: throw SessionInvalid) ?: throw ReceiverNotFound
    }

suspend fun ApplicationCall.verifyOrGetChild(): PersonDbo {
    val person = verifySession()

    if (PermissionHelper.getLevel(person.accessLevel) != PermissionLevel.Parent) return person

    val studentRef = dbQuery(logger) { HierarchyDao.getLower(person.id) }
        ?: throw UserNotFound

    return AuthManager.getUserById(studentRef.studentId)
}
