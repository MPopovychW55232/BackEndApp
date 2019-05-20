package com.makki.eschool.routing.announcements

import com.makki.eschool.database.dao.AnnouncementDao
import com.makki.eschool.database.dao.AnnouncementDbo
import com.makki.eschool.dbQuery
import com.makki.eschool.logger

object AnnouncementsManager {

    suspend fun addAnnouncement(dbo: AnnouncementDbo) =
        dbQuery(logger) {
            AnnouncementDao.insert(dbo)
        }

    suspend fun getAnnouncements(): List<AnnouncementDbo> =
        dbQuery(logger) {
            return@dbQuery AnnouncementDao.getAll()
        }

}
