package ru.deltadelete.netial.routes.roles

data class RoleRequest(
    val name: String,
    val permissions: Long,
    val description: String = "",
)