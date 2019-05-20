package com.makki.eschool

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

suspend inline fun <T> dbQuery(
    log: Logger = logger,
    noinline fallback: (() -> T)? = null,
    crossinline block: () -> T
): T {
    return try {
        withContext(Dispatchers.IO) { transaction { block() } }
    } catch (e: Exception) {
        log.error(e.message)
        return fallback?.invoke() ?: throw e
    }
}

suspend inline fun <T> apiAsync(
    log: Logger = logger,
    noinline fallback: (() -> T)? = null,
    crossinline block: suspend () -> T
): Deferred<T> {
    val job = GlobalScope.async(coroutineContext) {
        try {
            block()
        } catch (e: Exception) {
            if (fallback != null) {
                fallback()
            }
            throw e
        }
    }
    job.invokeOnCompletion {
        if (it != null && it !is CancellationException) {
            if (fallback != null) {
                log.warn("Handled exception in apiAsync()", it)
            } else {
                log.error("Unhandled exception in apiAsync()", it)
            }
        }
    }
    return job
}

fun DateTime.monthIndex(): Int {
    val currentMonth = monthOfYear

    return currentMonth + year * 12
}

fun <T> ResultRow.getSafe(c: Expression<T>, fallback: T): T {
    return if (this.hasValue(c)) this[c]
    else fallback
}

fun <T> UpdateStatement.setIfValid(column: Column<T>, value: T) {
    if (value is String && value.isNotBlank()) {
        this[column] = value
    }
}

suspend inline fun <T> callAndAwait(
    log: Logger = logger,
    noinline fallback: (() -> T)? = null,
    context: CoroutineContext = Dispatchers.IO,
    crossinline block: () -> T
): T {
    return try {
        withContext(context) { block() }
    } catch (e: Exception) {
        log.error(e.message)
        return fallback?.invoke() ?: throw e
    }
}

fun <K> String?.toJsonMap(): Map<String, K>? {
    if (this == null || this.isEmpty()) {
        return emptyMap()
    }
    try {
        return ObjectMapper().readValue(this, HashMap<String, K>()::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return emptyMap()
}

fun String.splitPair(ch: Char): Pair<String, String>? = indexOf(ch).let { idx ->
    when (idx) {
        -1 -> null
        else -> Pair(take(idx), drop(idx + 1))
    }
}

suspend fun String?.doIfEmpty(block: suspend () -> Unit): String? {
    if (this == null || this.isEmpty()) {
        block.invoke()
        return null
    }
    return this
}