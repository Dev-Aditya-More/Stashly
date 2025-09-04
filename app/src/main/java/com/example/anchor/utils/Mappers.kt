package com.example.anchor.utils

import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.LinkEntity
import com.example.anchor.data.local.SavedItem

fun LinkEntity.toSavedItem() = SavedItem(
    id = id,
    url = url,
    title = title,
    contentType = ContentType.LINK,
    createdAt = createdAt
)

fun SavedItem.toLinkEntity() = LinkEntity(
    id = id,
    url = url,
    title = title,
    summary = null,
    createdAt = createdAt
)
