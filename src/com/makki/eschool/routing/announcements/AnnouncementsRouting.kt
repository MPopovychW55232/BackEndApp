package com.makki.eschool.routing.announcements

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.makki.eschool.base.ApiException
import com.makki.eschool.base.ApiException.Companion.NotAuthorized
import com.makki.eschool.base.Permission
import com.makki.eschool.base.PermissionHelper
import com.makki.eschool.database.dao.AnnouncementDbo
import com.makki.eschool.routing.verifySession
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.applyAnnouncementsRouting() {

    route("/announcements") {
        get {
            call.respond(mapOf("announcements" to AnnouncementsManager.getAnnouncements()))
        }

        post("/announce") {
            val sender = call.verifySession()
            if (!PermissionHelper.checkPermission(sender, Permission.CreateAnnouncements)) {
                throw NotAuthorized
            }

            val message = call.receiveOrNull<Announcement>()

            if (message == null || !message.isValid()) {
                throw ApiException.invalidPost("ensure title, message, senderName arguments are present and valid")
            }

            if (message.senderName.isBlank()) {
                message.senderName = sender.name
            }

            val dbo = AnnouncementDbo(
                0,
                sender.id,
                message.senderName,
                message.title,
                message.message,
                System.currentTimeMillis()
            )

            AnnouncementsManager.addAnnouncement(dbo)

            call.respond(mapOf("error_code" to 0, "message" to "success"))
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Announcement(
    var title: String = "",
    var message: String = "",
    var senderName: String = ""
) {
    fun isValid() = message.isNotBlank()
}