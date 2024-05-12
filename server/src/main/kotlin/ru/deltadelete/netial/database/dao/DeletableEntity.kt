package ru.deltadelete.netial.database.dao

import kotlinx.datetime.Instant

/**
 * Удаляемая сущность
 * @property isDeleted Признак удаления
 * @property creationDate Дата создания
 * @property deletionDate Дата удаления
 */
interface DeletableEntity {
    var isDeleted: Boolean
    var creationDate: Instant
    var deletionDate: Instant?
}