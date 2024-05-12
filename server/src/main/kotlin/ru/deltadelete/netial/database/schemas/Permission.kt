package ru.deltadelete.netial.database.schemas

import java.util.*

/**
 * Битовая маска для разрешений
 * Лимитом является 0b111111111111111111111111111111111111111111111111111111111111111L или 63 значения
 * Значения начинающиеся на SELF относятся к объектам созданным текущим пользователем
 */
enum class Permission(val value: Long) {
    // ROLE
    CREATE_ROLE(1),
    MODIFY_ROLE(2),
    REMOVE_ROLE(4),
    // GROUP
    CREATE_GROUP(8),
    MODIFY_GROUP(16),
    REMOVE_GROUP(32),
    // POST
    CREATE_POST(64),
    MODIFY_POST(128),
    REMOVE_POST(256),
    // COMMENT
    CREATE_COMMENT(1024),
    MODIFY_COMMENT(2048),
    REMOVE_COMMENT(4096),
    // USER
    CREATE_USER(8192),
    REMOVE_USER(16384),
    MODIFY_USER(32768),
    // SELF REMOVE
    SELF_REMOVE_POST(65536),
    SELF_REMOVE_COMMENT(131072),
    SELF_REMOVE_USER(262144),
    // SELF MODIFY
    SELF_MODIFY_USER(524288),
    SELF_MODIFY_POST(1048576),
    SELF_MODIFY_COMMENT(2097152),
    // SELF CREATE
    SELF_CREATE_POST(4194304),
    SELF_CREATE_COMMENT(8388608),
    // ATTACHMENT
    CREATE_ATTACHMENT(16777216),
    MODIFY_ATTACHMENT(33554432),
    REMOVE_ATTACHMENT(67108864),
    // SELF ATTACHMENT
    SELF_CREATE_ATTACHMENT(134217728),
    SELF_MODIFY_ATTACHMENT(268435456),
    SELF_REMOVE_ATTACHMENT(536870912),;

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