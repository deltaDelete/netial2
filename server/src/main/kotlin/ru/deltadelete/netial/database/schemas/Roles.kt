package ru.deltadelete.netial.database.schemas

import ru.deltadelete.netial.database.schemas.Roles.description
import ru.deltadelete.netial.database.schemas.Roles.name
import ru.deltadelete.netial.database.schemas.Roles.permissions

/**
 * Роли
 * @property name Название роли
 * @property description Описание роли
 * @property permissions Битовая маска для разрешений
 */
object Roles : DeletableLongIdTable("roles") {
    val name = varchar("name", length = 50)
    val description = varchar("description", length = 255)
    val permissions = long("permissions").default(0L)
    val isDefault = bool("is_default").default(false)
}

