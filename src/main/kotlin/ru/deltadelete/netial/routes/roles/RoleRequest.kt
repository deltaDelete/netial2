package ru.deltadelete.netial.routes.roles

import ru.deltadelete.netial.database.schemas.Permission
import java.util.*

data class RoleRequest(
    val name: String,
    val permissions: EnumSet<Permission>,
    val description: String = "",
)