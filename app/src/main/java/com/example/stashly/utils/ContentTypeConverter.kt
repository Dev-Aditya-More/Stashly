package com.example.stashly.utils

import androidx.room.TypeConverter
import com.example.stashly.data.local.ContentType

class ContentTypeConverter {
    @TypeConverter
    fun fromContentType(type: ContentType): String = type.name

    @TypeConverter
    fun toContentType(value: String): ContentType = ContentType.valueOf(value)
}
