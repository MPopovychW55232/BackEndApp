package com.makki.eschool.routing.schedule

import com.makki.eschool.apiAsync
import com.makki.eschool.base.ApiException
import com.makki.eschool.base.ApiException.Companion.InvalidTimeRange
import com.makki.eschool.database.dao.schedule.LessonDbo
import com.makki.eschool.logger
import com.makki.eschool.monthIndex
import com.makki.eschool.routing.verifyOrGetChild
import com.makki.eschool.routing.verifySession
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import org.joda.time.DateTime

fun Route.applyScheduleRouting() {
    route("/schedule") {

        get("/periods") {
            val person = call.verifyOrGetChild()

            val periods = ScheduleManager.getAllPeriods(person.id)

            call.respond(mapOf("periods" to periods))
        }

        get("/periods/active") {
            val person = call.verifyOrGetChild()

            val periods = ScheduleManager.getActivePeriods(person.id)

            call.respond(mapOf("periods" to periods))
        }

        get ("/periods/{id}") {
            call.verifyOrGetChild()

            val periodId = call.parameters["id"]?.toIntOrNull() ?: throw ApiException.missingParam("id")

            val period = ScheduleManager.getPeriodById(periodId)

            call.respond(mapOf("period" to period))
        }

        get("/subjects") {
            val person = call.verifyOrGetChild()

            val periodId = call.request.queryParameters["period_id"]?.toIntOrNull()
            val subjects = if (periodId == null) {
                val periods = ScheduleManager.getActivePeriods(person.id)
                periods.map { apiAsync(logger) { ScheduleManager.getSubjectsForPeriod(it.id) } }.awaitAll().flatten()
            } else {
                ScheduleManager.getSubjectsForPeriod(periodId)
            }

            call.respond(mapOf("subjects" to subjects))
        }

        get("/subjects/{id}") {
            call.verifyOrGetChild()

            val subjectId = call.parameters["id"]?.toIntOrNull() ?: throw ApiException.missingParam("id")

            val subject = ScheduleManager.getSubjectById(subjectId)

            call.respond(mapOf("subject" to subject))
        }

        get("/lessons") {
            call.verifyOrGetChild()

            val start = call.request.queryParameters["start"]?.toLongOrNull()
                ?: throw ApiException.missingParam("start")
            val end = call.request.queryParameters["end"]?.toLongOrNull()
                ?: throw ApiException.missingParam("end")

            val periodId = call.request.queryParameters["period_id"]?.toIntOrNull()
            val subjectId = call.request.queryParameters["subject_id"]?.toIntOrNull()

            var firstMonth = DateTime(start).monthIndex()
            val lastMonth = DateTime(end).monthIndex()
            if (end < start) throw InvalidTimeRange

            val asyncList = ArrayList<Deferred<List<LessonDbo>>>()
            when {
                subjectId != null -> do {
                    asyncList.add(apiAsync(logger) {
                        ScheduleManager.getLessonsMonthBySubject(subjectId, firstMonth)
                    })
                    firstMonth++
                } while (firstMonth < lastMonth)
                periodId != null -> do {
                    asyncList.add(apiAsync(logger) {
                        ScheduleManager.getLessonsMonthByPeriod(periodId, firstMonth)
                    })
                    firstMonth++
                } while (firstMonth < lastMonth)
                else -> {
                    throw ApiException.missingParam("subject_id or period_id")
                }
            }

            val lessons = asyncList.awaitAll().flatten().filter {
                it.start in (start + 1) until end
            }
            call.respond(mapOf("lessons" to lessons))
        }

    }

}