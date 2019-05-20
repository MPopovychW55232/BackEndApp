package com.makki.eschool.database.dao.schedule

import org.jetbrains.exposed.sql.*

object LessonsDao : Table("lessons_table") {

    val id = integer("id").primaryKey().autoIncrement()
    val subjectId = (integer("subject_id") references SubjectDao.id)
    val subjectName = (varchar("subject_name", 60) references SubjectDao.name)
    val periodId = (integer("period_id") references PeriodDao.id)
    val monthId = integer("month_id")
    val start = long("start_time")
    val duration = long("duration")
    val extra = varchar("extra", 120)

    fun getById(lessonId: Int): LessonDbo? {
        return LessonsDao
            .select { id eq lessonId }
            .firstOrNull()?.build()
    }

    fun getBySubject(subject: Int): List<LessonDbo> {
        return LessonsDao
            .select { subjectId eq subject }
            .map { it.build() }
    }

    fun getByPeriod(period: Int): List<LessonDbo> {
        return LessonsDao
            .select { periodId eq period }
            .map { it.build() }
    }

    fun getByPeriodAndMonth(period: Int, monthIndex: Int): List<LessonDbo> {
        return LessonsDao
            .select { (periodId eq period) and (monthId eq monthIndex) }
            .map { it.build() }
    }

    fun getBySubjectAndMonth(subject: Int, monthIndex: Int): List<LessonDbo> {
        return LessonsDao
            .select { (subjectId eq subject) and (monthId eq monthIndex) }
            .map { it.build() }
    }

    fun insert(lesson: LessonDbo) {
        LessonsDao.insert {
            it[subjectId] = lesson.subjectId
            it[subjectName] = lesson.subjectName
            it[periodId] = lesson.periodId
            it[monthId] = lesson.monthId
            it[start] = lesson.start
            it[duration] = lesson.duration
            it[extra] = lesson.extra
        }
    }

    fun insert(list: List<LessonDbo>) {
        LessonsDao.batchInsert(list) { lesson ->
            this[subjectId] = lesson.subjectId
            this[subjectName] = lesson.subjectName
            this[periodId] = lesson.periodId
            this[monthId] = lesson.monthId
            this[start] = lesson.start
            this[duration] = lesson.duration
            this[extra] = lesson.extra
        }
    }

    private fun ResultRow.build(): LessonDbo {
        return LessonDbo(
            get(id),
            get(subjectId),
            get(subjectName),
            get(periodId),
            get(monthId),
            get(start),
            get(duration),
            get(extra)
        )
    }

}

data class LessonDbo(
    val id: Int,
    val subjectId: Int,
    val subjectName: String,
    val periodId: Int,
    val monthId: Int,
    val start: Long,
    val duration: Long,
    val extra: String
) {

    val end = start + duration

}
