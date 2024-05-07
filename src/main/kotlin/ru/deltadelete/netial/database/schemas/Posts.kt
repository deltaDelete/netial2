package ru.deltadelete.netial.database.schemas

/**
 * Публикация
 * @property text Содержимое публикации
 * @property user Пользователь, создавший публикацию
 * @property likes Количество лайков
 * @property comments Количество комментариев
 * @property isArticle Является ли публикация статьей
 */
object Posts : DeletableLongIdTable("posts") {
    val text = text("text")
    val user = reference("user_id", Users)
    val likes = integer("likes").default(0)
    val comments = integer("comments").default(0)
    val isArticle = bool("is_article").default(false)
}

