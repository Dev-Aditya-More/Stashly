package com.example.anchor.utils

import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.LinkEntity
import com.example.anchor.data.local.SavedItem

fun String.toLinkEntity(): LinkEntity {
    return LinkEntity(
        url = this,
        title = null
    )
}
fun LinkEntity.toSavedItem(): SavedItem {
    return SavedItem(
        url = this.url,
        title = this.title,
        type = ContentType.LINK // later you can infer type if it's YT, article etc.
    )
}

fun List<LinkEntity>.toSavedItems(): List<SavedItem> {
    return this.map { it.toSavedItem() }
}