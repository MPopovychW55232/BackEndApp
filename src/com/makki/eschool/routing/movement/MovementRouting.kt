package com.makki.eschool.routing.movement

import com.makki.eschool.apiAsync
import com.makki.eschool.base.ApiException
import com.makki.eschool.base.ApiException.Companion.NotAuthorized
import com.makki.eschool.base.ApiException.Companion.SessionMissing
import com.makki.eschool.database.dao.movement.MovementDbo
import com.makki.eschool.logger
import com.makki.eschool.monthIndex
import com.makki.eschool.routing.verifySession
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import org.joda.time.DateTime

fun Route.applyMovementRouting() {

    route("/movement") {
        get {
            val person = call.verifySession()

            val start = call.request.queryParameters["start"]?.toLongOrNull()
                ?: throw ApiException.missingParam("start")
            val end = call.request.queryParameters["end"]?.toLongOrNull()
                ?: throw ApiException.missingParam("end")

            var firstMonth = DateTime(start).monthIndex()
            val lastMonth = DateTime(end).monthIndex()
            if (end < start) throw ApiException.InvalidTimeRange

            val asyncList = ArrayList<Deferred<List<MovementDbo>>>()

            do {
                asyncList.add(apiAsync(logger) {
                    MovementManager.getRecordsByMonth(person.id, firstMonth)
                })
                firstMonth++
            } while (firstMonth <= lastMonth)

            call.respond(mapOf("movement" to asyncList.awaitAll().flatten()))
        }

        post("/moved") {
            val sid = call.request.queryParameters["sid"] ?: throw SessionMissing

            if (sid != "90628aa2-68e6-4bce-9549-6a6d27678364") throw NotAuthorized

            val personId = call.request.queryParameters["person_id"]?.toIntOrNull()
                ?: throw ApiException.missingParam("person_id")
            val entered = call.request.queryParameters["entered"]?.toBoolean()
                ?: throw ApiException.missingParam("entered")
            val gate = call.request.queryParameters["gate"] ?: ""

            val now = DateTime()
            val month = now.monthIndex()
            val day = now.dayOfYear

            val record = MovementDbo(0, personId, gate, month, day, now.millis, entered)
            MovementManager.addRecords(listOf(record))

            call.respond(mapOf("error_code" to 0, "message" to "success"))
        }
    }

}
