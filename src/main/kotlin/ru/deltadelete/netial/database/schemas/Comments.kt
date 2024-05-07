package ru.deltadelete.netial.database.schemas

/**
 * Описание таблицы комментариев
 * @property text Текст комментария
 * @property user Пользователь, создавший комментарий
 * @property post Пост, к которому прикреплен комментарий
 */
object Comments : DeletableLongIdTable("comments") {
    val text = text("text")
    val user = reference("user_id", Users)
    val post = reference("post_id", Posts)
    val likes = integer("likes").default(0)
}