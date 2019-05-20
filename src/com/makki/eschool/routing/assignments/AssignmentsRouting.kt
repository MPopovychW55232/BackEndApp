package com.makki.eschool.routing.assignments

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.makki.eschool.base.ApiException
import com.makki.eschool.base.Permission
import com.makki.eschool.base.PermissionHelper
import com.makki.eschool.database.dao.AssignmentDbo
import com.makki.eschool.routing.verifyOrGetChild
import com.makki.eschool.routing.verifySession
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.applyAssignmentsRouting() {

    route("/assignments") {
        get {
            val receiver = call.verifyOrGetChild()

            val assignments = AssignmentsManager.getAssignments(receiver.id)

            call.respond(mapOf("assignments" to assignments))
        }

        post("/assign") {
            val sender = call.verifySession()
            if (!PermissionHelper.checkPermission(sender, Permission.CreateAssignments)) {
                throw ApiException.NotAuthorized
            }

            val assignment = call.receiveOrNull<Assignment>()

            if (assignment == null || !assignment.isValid()) {
                throw ApiException.invalidPost("ensure receiverId, title, message, senderName arguments are present and valid")
            }

            if (assignment.senderName.isBlank()) {
                assignment.senderName = sender.name
            }

            val dbo = AssignmentDbo(
                0,
                assignment.receiverId,
                sender.id,
                assignment.senderName,
                assignment.title,
                assignment.message,
                System.currentTimeMillis()
            )

            AssignmentsManager.addAssignment(dbo)

            call.respond(mapOf("error_code" to 0, "message" to "success"))
        }
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Assignment(
    var receiverId: Int = -1,
    var senderName: String = "",
    var title: String = "",
    var message: String = ""
) {
    fun isValid() = receiverId != -1 && message.isNotBlank()
}
