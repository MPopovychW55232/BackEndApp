package com.makki.eschool.routing.grades

import com.makki.eschool.base.ApiException
import com.makki.eschool.routing.verifyOrGetChild
import com.makki.eschool.routing.verifySession
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

fun Route.applyGradeRouting() {
    route("/grades") {
        get("/period") {
            val person = call.verifyOrGetChild()

            val periodId = call.request.queryParameters["period_id"]?.toIntOrNull()
                ?: throw ApiException.missingParam("period_id")

            call.respond(mapOf("grades" to GradesManager.getByPeriod(person.id, periodId)))
        }
    }
}