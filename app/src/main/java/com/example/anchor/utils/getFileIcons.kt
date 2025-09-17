package com.example.anchor.utils

import com.example.stashly.R

fun getFileIconRes(fileName: String): Int {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return when (ext) {
        "jpg", "jpeg", "png" -> R.drawable.ic_img
        "pdf" -> R.drawable.ic_pdf
        "doc", "docx" -> R.drawable.ic_word
        "txt" -> R.drawable.ic_text
        "mp4", "mkv" -> R.drawable.ic_video
        "mp3", "wav" -> R.drawable.ic_audio
        else -> R.drawable.ic_file // fallback
    }
}

