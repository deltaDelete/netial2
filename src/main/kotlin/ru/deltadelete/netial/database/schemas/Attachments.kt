package ru.deltadelete.netial.database.schemas

import org.jetbrains.exposed.sql.Column

/**
 * Представляет собой вложение
 * @property id Уникальный идентификатор, файлы в хранилице должны иметь такое же имя, что и их идентификатор
 * @property mimeType Один из [Mime-Type](https://developer.mozilla.org/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types)
 * @property hash SHA256 хеш файла
 * @property user Пользователь загрузивший вложение
 */
object Attachments : DeletableLongIdTable("attachments") {
    val mimeType: Column<String> = varchar("mime_type", length = 255)
    val name: Column<String> = varchar("name", length = 255)
    val hash: Column<String> = char("hash", length = 64)
    val size = long("size").default(0)
    val user = reference("user_id", Users)
}