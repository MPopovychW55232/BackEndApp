package com.makki.eschool.base

class ApiException(val code: Int, val msg: String) : Exception(msg) {

    companion object {
        val InvalidMethod = ApiException(-1, "Unknown or invalid api method")

        val SessionMissing = ApiException(11, "Session id not provided")
        val LoginMissing = ApiException(12, "Login not provided")
        val PasswordMissing = ApiException(13, "Password not provided")

        val SessionInvalid = ApiException(21, "Session is not valid")
        val InvalidCredentials = ApiException(22, "Invalid credentials")

        val SenderNotFound = ApiException(32, "Couldn't find the sender user")
        val ReceiverNotFound = ApiException(32, "Couldn't find the receiver")
        val PeriodNotFound = ApiException(33, "Couldn't find a period")
        val SubjectNotFound = ApiException(33, "Couldn't find a subject")
        val LessonNotFound = ApiException(33, "Couldn't find a lesson")

        val SessionError = ApiException(51, "Couldn't create session")
        val UserNotFound = ApiException(52, "Couldn't fetch a user")
        val NotificationNotFound = ApiException(53, "Couldn't find a notification with such id")
        val InvalidTimeRange = ApiException(54, "Invalid time range")

        val NotAuthorized = ApiException(99, "Not authorized for such operation")

        fun missingParam(param: String) = ApiException(19, "Param with name '$param' was not provided")

        fun invalidPost(msg: String) = ApiException(18, "Invalid post: $msg")
    }

}