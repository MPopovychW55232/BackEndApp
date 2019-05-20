package com.makki.eschool.base

import com.makki.eschool.database.dao.auth.PersonDbo

object PermissionHelper {

    fun Int.toLevel(): PermissionLevel {
        return PermissionLevel.values().find { it.level == this } ?: PermissionLevel.None
    }

    fun getLevel(value: Int): PermissionLevel = value.toLevel()

    fun checkPermission(person: PersonDbo, permission: Permission): Boolean {
        when (person.accessLevel.toLevel()) {
            PermissionLevel.None -> return false
            PermissionLevel.Student -> return false
            PermissionLevel.Parent -> return false
            PermissionLevel.StudentHead -> return when (permission) {
                Permission.SendNotification -> true
                Permission.CreateAssignments -> true
                else -> false
            }
            PermissionLevel.Teacher -> return when (permission) {
                Permission.SendNotification -> true
                Permission.CreateAssignments -> true
                Permission.ChangeSchedule -> true
                else -> false
            }
            PermissionLevel.SystemManager -> return when (permission) {
                Permission.SendNotification -> true
                Permission.CreateAssignments -> true
                Permission.ChangeSchedule -> true
                Permission.ManageStudents -> true
                Permission.CreateAnnouncements -> true
                else -> false
            }
            PermissionLevel.Admin -> return true
            else -> return false
        }
    }

}

enum class Permission {
    SendNotification,
    CreateAssignments,
    CreateAnnouncements,
    ChangeSchedule,
    ManageStudents
}

enum class PermissionLevel(val level: Int) {
    None(-1),
    Student(0),
    Parent(1),
    StudentHead(2),
    Teacher(9),
    SystemManager(80),
    Admin(99)
}