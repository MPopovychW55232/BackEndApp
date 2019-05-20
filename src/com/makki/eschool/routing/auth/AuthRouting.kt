package com.makki.eschool.routing.auth

import com.makki.eschool.base.ApiException
import com.makki.eschool.base.ApiException.Companion.LoginMissing
import com.makki.eschool.base.ApiException.Companion.PasswordMissing
import com.makki.eschool.routing.verifySession
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.applyAuthRouting() {
    get("/login") {
        val login = call.request.queryParameters["login"] ?: throw LoginMissing
        val password = call.request.queryParameters["password"] ?: throw PasswordMissing

        val user = AuthManager.verifyAndGetUser(login, password)
        val session = AuthManager.createOrGetSession(user)

        call.respond(mapOf("sid" to session.sid))
    }

    get("/person/{id}") {
        call.verifySession()

        val id = call.parameters["id"]?.toIntOrNull() ?: throw ApiException.missingParam("id")

        call.respond(mapOf("user" to AuthManager.getUserById(id)))
    }
}


