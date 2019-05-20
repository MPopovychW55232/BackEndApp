package com.makki.eschool.database.dao.auth

import org.jetbrains.exposed.sql.*

object SecurityDao : Table("security_table") {

    val login = (varchar("login", 18) references PersonDao.login).primaryKey()
    val password = varchar("password", 18)

    fun insert(authInfo: SecurityDbo) {
        SecurityDao.insert {
            it[login] = authInfo.login
            it[password] = authInfo.password
        }
    }

    fun update(authInfo: SecurityDbo) {
        SecurityDao.update({ login eq authInfo.login }) {
            it[login] = authInfo.login
            it[password] = authInfo.password
        }
    }

    fun validate(authInfo: SecurityDbo): Boolean {
        val existing = getByLogin(authInfo.login)

        return authInfo.password == existing?.password
    }

    private fun getByLogin(loginS: String): SecurityDbo? {
        return SecurityDao
            .select { login eq loginS }
            .firstOrNull()?.build() ?: return null
    }

    private fun ResultRow.build(): SecurityDbo {
        return SecurityDbo(
            get(login),
            get(password)
        )
    }
}

data class SecurityDbo(val login: String, val password: String)

