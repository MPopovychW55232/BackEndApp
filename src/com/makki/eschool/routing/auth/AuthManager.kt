package com.makki.eschool.routing.auth

import com.makki.eschool.base.ApiException
import com.makki.eschool.database.dao.auth.*
import com.makki.eschool.dbQuery
import com.makki.eschool.logger
import java.util.*

object AuthManager {

    suspend fun register(personDbo: PersonDbo, password: String) =
        dbQuery(logger) {
            PersonDao.insert(personDbo)
            SecurityDao.insert(
                SecurityDbo(
                    personDbo.login,
                    password
                )
            )
        }

    suspend fun registerParent(personDbo: PersonDbo, student: PersonDbo, password: String) {
        val login = personDbo.login
        register(personDbo, password)
        val person = getUserByLogin(login)

        dbQuery(logger) {
            HierarchyDao.insert(HierarchyDbo(person.id, student.id))
        }
    }

    suspend fun getUserById(person: Int): PersonDbo =
        dbQuery(logger) {
            PersonDao.getById(person) ?: throw ApiException.UserNotFound
        }

    suspend fun getUserByLogin(login: String): PersonDbo =
        dbQuery(logger) {
            PersonDao.getByLogin(login) ?: throw ApiException.UserNotFound
        }

    suspend fun verifyAndGetUser(login: String, password: String): PersonDbo {
        dbQuery(logger) {
            if (!SecurityDao.validate(
                    SecurityDbo(
                        login,
                        password
                    )
                )
            ) {
                throw ApiException.InvalidCredentials
            }
        }

        return getUserByLogin(login)
    }

    suspend fun createOrGetSession(user: PersonDbo): SessionDbo =
        dbQuery(logger) {
            val session = SessionDao.getById(user.id)
            if (session != null) return@dbQuery session

            val randomSession = UUID.randomUUID().toString()
            SessionDao.insert(
                SessionDbo(
                    user.id,
                    randomSession
                )
            )

            val session2 = SessionDao.getById(user.id)
            if (session2 != null) return@dbQuery session2
            else throw ApiException.SessionError
        }

}