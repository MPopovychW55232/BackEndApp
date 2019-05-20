package com.makki.eschool.routing.schedule

import com.makki.eschool.base.ApiException.Companion.LessonNotFound
import com.makki.eschool.base.ApiException.Companion.PeriodNotFound
import com.makki.eschool.base.ApiException.Companion.SubjectNotFound
import com.makki.eschool.database.dao.schedule.*
import com.makki.eschool.dbQuery
import com.makki.eschool.logger
import com.makki.eschool.routing.auth.AuthManager

object ScheduleManager {

    suspend fun getPeriodByName(periodName: String): PeriodDbo =
        dbQuery(logger) {
            return@dbQuery PeriodDao.getByName(periodName) ?: throw PeriodNotFound
        }

    suspend fun getPeriodById(period: Int): PeriodDbo =
        dbQuery(logger) {
            return@dbQuery PeriodDao.getById(period) ?: throw PeriodNotFound
        }

    suspend fun addPeriod(period: PeriodDbo) =
        dbQuery(logger) {
            PeriodDao.insert(period)
        }

    suspend fun addPeriodAndGet(period: PeriodDbo): PeriodDbo {
        val name = period.name
        addPeriod(period)
        return getPeriodByName(name)
    }

    suspend fun getActivePeriods(person: Int): List<PeriodDbo> =
        dbQuery(logger) {
            val time = System.currentTimeMillis()
            val periods = PersonPeriodDao.getAllPeriods(person).map { it.periodId }

            return@dbQuery PeriodDao.getByIds(periods).filter { it.startTime < time && time < it.endTime }
        }

    suspend fun getAllPeriods(person: Int): List<PeriodDbo> =
        dbQuery(logger) {
            val periods = PersonPeriodDao.getAllPeriods(person).map { it.periodId }

            return@dbQuery PeriodDao.getByIds(periods)
        }

    suspend fun addPersonToPeriod(personId: Int, periodId: Int) {
        val person = AuthManager.getUserById(personId)
        val period = getPeriodById(periodId)

        dbQuery(logger) {
            PersonPeriodDao.insert(PersonPeriodDbo(0, person.id, period.id))
        }
    }

    suspend fun addSubjectToPeriod(subjectDbo: SubjectDbo) {
        getPeriodById(subjectDbo.periodId)

        dbQuery(logger) {
            SubjectDao.insert(subjectDbo)
            PeriodDao.setAsModified(subjectDbo.periodId)
        }
    }

    suspend fun getSubjectById(subjectId: Int): SubjectDbo =
        dbQuery(logger) {
            return@dbQuery SubjectDao.getById(subjectId) ?: throw SubjectNotFound
        }

    suspend fun getSubjectsForPeriod(periodId: Int): List<SubjectDbo> {
        val period = getPeriodById(periodId)

        return dbQuery(logger) {
            return@dbQuery SubjectDao.getByPeriod(period.id)
        }
    }

    suspend fun addLessonToSubject(lessonDbo: LessonDbo) {
        getSubjectById(lessonDbo.subjectId)

        return dbQuery(logger) {
            LessonsDao.insert(lessonDbo)
            PeriodDao.setAsModified(lessonDbo.periodId)
        }
    }

    suspend fun addLessonsToSubject(list: List<LessonDbo>) =
        dbQuery(logger) {
            LessonsDao.insert(list)
            val periods = list.map { it.periodId }.distinct()
            for (id in periods) {
                PeriodDao.setAsModified(id)
            }
        }

    suspend fun getLessonById(lessonId: Int): LessonDbo =
        dbQuery(logger) {
            return@dbQuery LessonsDao.getById(lessonId) ?: throw LessonNotFound
        }

    suspend fun getLessonsBySubject(subjectId: Int): List<LessonDbo> {
        val subject = getSubjectById(subjectId)

        return dbQuery(logger) {
            return@dbQuery LessonsDao.getBySubject(subject.id)
        }
    }

    suspend fun getLessonsMonthBySubject(subjectId: Int, monthIndex: Int): List<LessonDbo> {
        val subject = getSubjectById(subjectId)

        return dbQuery(logger) {
            return@dbQuery LessonsDao.getBySubjectAndMonth(subject.id, monthIndex)
        }
    }

    suspend fun getLessonsMonthByPeriod(periodId: Int, monthIndex: Int): List<LessonDbo> {
        val period = getPeriodById(periodId)

        return dbQuery(logger) {
            return@dbQuery LessonsDao.getByPeriodAndMonth(period.id, monthIndex)
        }
    }

}
