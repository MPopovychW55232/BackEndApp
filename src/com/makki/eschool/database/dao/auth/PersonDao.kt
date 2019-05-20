package com.makki.eschool.database.dao.auth

import org.jetbrains.exposed.sql.*

object PersonDao : Table("person_table") {

    val id = integer("id").primaryKey().autoIncrement()
    val login = varchar("login", 18).primaryKey()
    val name = varchar("name", 50)
    val email = varchar("email", 100)
    val city = varchar("city", 50)
    val photo = text("photo")
    val phone1 = varchar("phone_1", 20)
    val phone2 = varchar("phone_2", 20)
    val extra = text("extra")
    val accessLevel = integer("access_level")

    fun getById(personId: Int): PersonDbo? {
        return PersonDao
            .select { id eq personId }
            .firstOrNull()?.build() ?: return null
    }

    fun getByLogin(loginString: String): PersonDbo? {
        return PersonDao
            .select { login eq loginString }
            .firstOrNull()?.build() ?: return null
    }

    fun insert(person: PersonDbo) {
        PersonDao.insert {
            it[login] = person.login
            it[name] = person.name
            it[email] = person.email
            it[city] = person.city
            it[photo] = person.photo
            it[phone1] = person.phone1
            it[phone2] = person.phone2
            it[extra] = person.extra
            it[accessLevel] = person.accessLevel
        }
    }

    private fun ResultRow.build(): PersonDbo {
        return PersonDbo(
            get(id),
            get(login),
            get(name),
            get(email),
            get(city),
            get(photo),
            get(phone1),
            get(phone2),
            get(extra),
            get(accessLevel)
        )
    }
}

data class PersonDbo(
    val id: Int,
    val login: String,
    val name: String,
    val email: String,
    val city: String,
    val photo: String,
    val phone1: String,
    val phone2: String,
    val extra: String,
    val accessLevel: Int
)