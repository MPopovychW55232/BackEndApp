package com.makki.eschool.routing.notifications

import com.makki.eschool.base.ApiException.Companion.NotificationNotFound
import com.makki.eschool.base.ApiException.Companion.ReceiverNotFound
import com.makki.eschool.base.ApiException.Companion.SenderNotFound
import com.makki.eschool.database.dao.NotificationDao
import com.makki.eschool.database.dao.NotificationDbo
import com.makki.eschool.database.dao.auth.PersonDao
import com.makki.eschool.dbQuery
import com.makki.eschool.logger

object NotificationManager {

    suspend fun addNotification(dbo: NotificationDbo) =
        dbQuery(logger) {
            PersonDao.getById(dbo.receiverId) ?: throw ReceiverNotFound
            PersonDao.getById(dbo.senderId) ?: throw SenderNotFound
            NotificationDao.insert(dbo)
        }

    suspend fun getNotifications(receiverId: Int): List<NotificationDbo> =
        dbQuery(logger) {
            return@dbQuery NotificationDao.getAllForReceiver(receiverId)
        }

    suspend fun getNotificationById(notificationId: Int?): NotificationDbo =
        dbQuery(logger) {
            if (notificationId == null) throw NotificationNotFound
            return@dbQuery NotificationDao.getById(notificationId) ?: throw NotificationNotFound
        }

    suspend fun markAsRead(notificationId: Int) = dbQuery(logger) {
        return@dbQuery NotificationDao.markAsRead(notificationId)
    }

}
