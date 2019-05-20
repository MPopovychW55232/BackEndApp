package com.makki.eschool.routing.notifications

import com.makki.eschool.base.ApiException
import com.makki.eschool.base.ApiException.Companion.NotAuthorized
import com.makki.eschool.base.ApiException.Companion.NotificationNotFound
import com.makki.eschool.routing.verifyOrGetChild
import com.makki.eschool.routing.verifySession
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

fun Route.applyNotificationRouting() {
    route("/notifications") {
        get {
            val receiver = call.verifyOrGetChild()

            val notification = NotificationManager.getNotifications(receiver.id)

            call.respond(mapOf("notifications" to notification))
        }

        get("/{id}") {
            val receiver = call.verifyOrGetChild()

            val id = call.parameters["id"]?.toIntOrNull() ?: throw ApiException.missingParam("id")

            val notification = NotificationManager.getNotificationById(id)

            if (receiver.id != notification.receiverId) throw NotAuthorized

            call.respond(mapOf("notification" to notification))
        }

        post("/{id}/read") {
            val receiver = call.verifyOrGetChild()

            val id = call.parameters["id"]?.toIntOrNull() ?: throw ApiException.missingParam("id")

            val notification = NotificationManager.getNotificationById(id)

            if (receiver.id != notification.receiverId) throw NotAuthorized

            NotificationManager.markAsRead(notification.id)

            call.respond(mapOf("error_code" to 0, "message" to "success"))
        }
    }
}