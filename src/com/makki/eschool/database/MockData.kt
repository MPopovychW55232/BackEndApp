package com.makki.eschool.database

import com.makki.eschool.base.PermissionLevel
import com.makki.eschool.database.dao.AnnouncementDbo
import com.makki.eschool.database.dao.GradeDbo
import com.makki.eschool.database.dao.NotificationDbo
import com.makki.eschool.database.dao.auth.PersonDbo
import com.makki.eschool.database.dao.movement.MovementDbo
import com.makki.eschool.database.dao.schedule.LessonDbo
import com.makki.eschool.database.dao.schedule.PeriodDbo
import com.makki.eschool.database.dao.schedule.SubjectDbo
import com.makki.eschool.monthIndex
import com.makki.eschool.routing.announcements.AnnouncementsManager
import com.makki.eschool.routing.auth.AuthManager
import com.makki.eschool.routing.grades.GradesManager
import com.makki.eschool.routing.movement.MovementManager
import com.makki.eschool.routing.notifications.NotificationManager
import com.makki.eschool.routing.schedule.ScheduleManager
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.joda.time.MutableDateTime
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.random.Random

fun asyncMockData() {
    runBlocking {
        initMockData()
    }
}

suspend fun initMockData() {
    AuthManager.register(
        PersonDbo(
            0, "studstud", "A. Abracadabra",
            "stud1@eschool.test", "", "",
            "", "", "",
            PermissionLevel.Student.level
        ), "studstud"
    )

    AuthManager.register(
        PersonDbo(
            0, "admin", "System administrator",
            "admin@eschool.test", "",
            "https://besplatka.ua/aws/23/52/00/21/administrator-v-salon-krasoty-photo-e85b.png", "", "", "",
            PermissionLevel.Admin.level
        ), "admin"
    )

    AuthManager.register(
        PersonDbo(
            0, "system", "System",
            "noreply@eschool.test", "",
            "", "", "", "",
            PermissionLevel.Admin.level
        ), "system"
    )

    val first = AuthManager.getUserByLogin("studstud")

    AuthManager.registerParent(
        PersonDbo(
            0, "parent-stud1", "N. Abracadabra",
            "parentstud1@eschool.test", "", "",
            "", "", "",
            PermissionLevel.Parent.level
        ), first, "1291412"
    )

    val admin = AuthManager.getUserByLogin("admin")
    val system = AuthManager.getUserByLogin("system")

    AnnouncementsManager.addAnnouncement(AnnouncementDbo(0, admin.id, "Message from Administrator", "Start of the system",
        "Greetings everybody!\n\n\n\n. Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test \n" +
                "Test Test Test Test Test Test Test Test Test Test Test Test Test Test \nTest Test Test Test Test Test Test Test ", System.currentTimeMillis()))

    NotificationManager.addNotification(
        NotificationDbo(
            0,
            first.id,
            admin.id,
            "Administrator",
            "Hello user,\n\n\nWelcome to the system",
            "Administrator",
            true,
            System.currentTimeMillis()
        )
    )

    NotificationManager.addNotification(
        NotificationDbo(
            0,
            first.id,
            system.id,
            "System no reply",
            "Greetings,",
            "Test of notifications",
            true,
            System.currentTimeMillis()
        )
    )

    val monthAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(231)
    val monthLater = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(131)
    val period = ScheduleManager.addPeriodAndGet(
        PeriodDbo(
            0,
            monthAgo,
            monthLater,
            "First period of 001D",
            "Test of schedule"
        )
    )

    ScheduleManager.addPersonToPeriod(first.id, period.id)

    /**
     * SUBJECTS
     */
    ScheduleManager.addSubjectToPeriod(
        SubjectDbo(
            0,
            period.id,
            "History",
            "Study of modern history, lead by A.Howtang"
        )
    )
    ScheduleManager.addSubjectToPeriod(
        SubjectDbo(
            0,
            period.id,
            "English language",
            "Linguistics, lead by N.Shamat"
        )
    )
    ScheduleManager.addSubjectToPeriod(
        SubjectDbo(
            0,
            period.id,
            "Modern art",
            "History and philosophy of art, lead by Z.Kant"
        )
    )

    val subjects = ScheduleManager.getSubjectsForPeriod(period.id)

    val subject1 = subjects.firstOrNull() ?: throw Exception("NO SUBJECT FOUND $subjects, period $period, user $first")
    GradesManager.addGrade(GradeDbo(0, first.id, period.id, subject1.id, System.currentTimeMillis(), 5.0f, "None"))
    val subject2 = subjects[1]
    GradesManager.addGrade(GradeDbo(0, first.id, period.id, subject2.id, System.currentTimeMillis(), 3.0f, "Test"))

    /**
     * LESSONS
     */

    val random = Random(System.currentTimeMillis())

    val subjectCount = subjects.size

    val lessons = ArrayList<LessonDbo>()
    var start = period.startTime
    var month = DateTime(start).monthIndex()
    while (start < period.endTime) {
        val nineHours = MutableDateTime(start).also {
            it.hourOfDay = 8
            it.minuteOfHour = 45
        }

        if (nineHours.dayOfWeek == 6 || nineHours.dayOfWeek == 7) {
            start += TimeUnit.DAYS.toMillis(1)
            month = DateTime(start).monthIndex()
            continue
        }

        for (i in 0 until 8) {
            val subject = subjects[random.nextInt(subjectCount)]
            lessons.add(
                LessonDbo(
                    0,
                    subject.id,
                    subject.name,
                    subject.periodId,
                    month,
                    nineHours.millis + i * TimeUnit.HOURS.toMillis(1),
                    TimeUnit.MINUTES.toMillis(45),
                    ""
                )
            )
        }
        start += TimeUnit.DAYS.toMillis(1)
        month = DateTime(start).monthIndex()
    }

    ScheduleManager.addLessonsToSubject(lessons)

    /**
     * MOVEMENT
     */

    val movement = ArrayList<MovementDbo>()
    var movementStart = period.startTime
    var movementMonth = DateTime(movementStart).monthIndex()

    val endTime = min(period.endTime, System.currentTimeMillis())
    while (movementStart < endTime) {
        val nineHours = MutableDateTime(movementStart).also {
            it.hourOfDay = 8
            it.minuteOfHour = 30
        }

        if (nineHours.dayOfWeek == 6 || nineHours.dayOfWeek == 7 || nineHours.millis > System.currentTimeMillis()) {
            movementStart += TimeUnit.DAYS.toMillis(1)
            movementMonth = DateTime(movementStart).monthIndex()
            continue
        }

        var entered = true
        var currentTime = nineHours.millis
        var last = MovementDbo(
            0,
            first.id,
            "test gate",
            movementMonth,
            nineHours.dayOfYear,
            currentTime,
            entered
        )

        val endAt = currentTime + TimeUnit.HOURS.toMillis(8)
        while ((currentTime < endAt) && currentTime < System.currentTimeMillis()) {
            last = MovementDbo(
                0,
                first.id,
                "test gate",
                movementMonth,
                nineHours.dayOfYear,
                currentTime,
                entered
            )
            currentTime += random.nextLong(TimeUnit.MINUTES.toMillis(140), TimeUnit.MINUTES.toMillis(220))
            entered = !entered
            movement.add(last)
            currentTime += random.nextLong(TimeUnit.MINUTES.toMillis(2), TimeUnit.MINUTES.toMillis(15))
        }

        if (last.entered && currentTime < System.currentTimeMillis()) {
            last = MovementDbo(
                0,
                first.id,
                "test gate",
                movementMonth,
                nineHours.dayOfYear,
                currentTime + TimeUnit.MINUTES.toMillis(2),
                false
            )
            movement.add(last)
        }

        movementStart += TimeUnit.DAYS.toMillis(1)
        movementMonth = DateTime(movementStart).monthIndex()
    }

    MovementManager.addRecords(movement)

}