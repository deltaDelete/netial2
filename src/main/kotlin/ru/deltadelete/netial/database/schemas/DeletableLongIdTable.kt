package ru.deltadelete.netial.database.schemas

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

/**
 * Базовый класс для таблиц с пометкой удаления
 * @property creationDate Дата создания
 * @property deletionDate Дата удаления
 * @property isDeleted Признак удаления
 */
open class DeletableLongIdTable(name: String, columnName: String = "id") : LongIdTable(name, columnName) {
    val creationDate = timestamp("creation_date")
    val deletionDate = timestamp("deletion_date").nullable()
    val isDeleted = bool("is_deleted").default(false)
}