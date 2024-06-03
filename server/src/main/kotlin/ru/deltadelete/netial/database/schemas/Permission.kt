package ru.deltadelete.netial.database.schemas

import java.util.*

/**
 * Битовая маска для разрешений
 * Лимитом является 0b111111111111111111111111111111111111111111111111111111111111111L или 63 значения
 * Значения начинающиеся на SELF относятся к объектам созданным текущим пользователем
 */
enum class Permission(val value: Long) {
    // ROLE
    CREATE_ROLE(1 shl 0),
    MODIFY_ROLE(1 shl 1),
    REMOVE_ROLE(1 shl 2),
    // GROUP
    CREATE_GROUP(1 shl 3),
    MODIFY_GROUP(1 shl 4),
    REMOVE_GROUP(1 shl 5),
    // POST
    CREATE_POST(1 shl 6),
    MODIFY_POST(1 shl 7),
    REMOVE_POST(1 shl 8),
    // COMMENT
    CREATE_COMMENT(1 shl 9),
    MODIFY_COMMENT(1 shl 10),
    REMOVE_COMMENT(1 shl 11),
    // USER
    CREATE_USER(1 shl 12),
    REMOVE_USER(1 shl 13),
    MODIFY_USER(1 shl 14),
    // SELF REMOVE
    SELF_REMOVE_POST(1 shl 15),
    SELF_REMOVE_COMMENT(1 shl 16),
    SELF_REMOVE_USER(1 shl 17),
    // SELF MODIFY
    SELF_MODIFY_USER(1 shl 18),
    SELF_MODIFY_POST(1 shl 19),
    SELF_MODIFY_COMMENT(1 shl 20),
    // SELF CREATE
    SELF_CREATE_POST(1 shl 21),
    SELF_CREATE_COMMENT(1 shl 22),
    // ATTACHMENT
    CREATE_ATTACHMENT(1 shl 23),
    MODIFY_ATTACHMENT(1 shl 24),
    REMOVE_ATTACHMENT(1 shl 25),
    // SELF ATTACHMENT
    SELF_CREATE_ATTACHMENT(1 shl 26),
    SELF_MODIFY_ATTACHMENT(1 shl 27),
    SELF_REMOVE_ATTACHMENT(1 shl 28),;

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