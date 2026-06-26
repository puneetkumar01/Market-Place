package com.puneet.marketplace.util

fun String.toCategoryLabel(): String =
    replace("-", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercaseChar() }
        }
