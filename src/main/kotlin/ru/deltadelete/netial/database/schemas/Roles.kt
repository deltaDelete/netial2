package ru.deltadelete.netial.database.schemas

import ru.deltadelete.netial.database.schemas.Roles.description
import ru.deltadelete.netial.database.schemas.Roles.name
import ru.deltadelete.netial.database.schemas.Roles.permissions
import java.util.*

/**
 * Роли
 * @property name Название роли
 * @property description Описание роли
 * @property permissions Битовая маска для разрешений
 */
object Roles: DeletableLongIdTable("roles") {
    val name = varchar("name", length = 50)
    val description = varchar("description", length = 255)
    val permissions = long("permissions").default(0L)
}

/**
 * Битовая маска для разрешений
 * Лимитом является 0b111111111111111111111111111111111111111111111111111111111111111L или 63 значения
 */
enum class Permission(val value: Long) {
    CREATE_ROLE     (1),
    MODIFY_ROLE     (2),
    REMOVE_ROLE     (4),
    CREATE_GROUP    (8),
    MODIFY_GROUP    (16),
    REMOVE_GROUP    (32),
    CREATE_POST     (64),
    MODIFY_POST     (128),
    REMOVE_POST     (256),
    CREATE_COMMENT  (1024),
    MODIFY_COMMENT  (2048),
    REMOVE_COMMENT  (4096),
    CREATE_USER     (8192),
    REMOVE_USER     (16384),
    MODIFY_USER     (32768);

    companion object {
        fun fromBitMask(mask: Long): EnumSet<Permission> {
            val permissions = EnumSet.noneOf(Permission::class.java)
            Permission.entries.forEach {
                if (mask and it.value != 0L) {
                    permissions.add(it)
                }
            }
            return permissions
        }
    }
}