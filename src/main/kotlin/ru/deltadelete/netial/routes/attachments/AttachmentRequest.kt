package ru.deltadelete.netial.routes.attachments

data class AttachmentRequest(
    val name: String,
    val mimeType: String,
    val size: Long = 0L,
    val userId: Long? = null,
)