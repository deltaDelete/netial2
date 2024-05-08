package ru.deltadelete.netial.database.dto

interface MappableDto<From, To> {
    fun from(from: From): To
}