package com.heronet.sellnet.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
object DateParser {
    private val parser by lazy { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") }
    private val formatter by lazy { SimpleDateFormat("dd/MM/yyyy hh:mm a") }
    fun getFormattedDate(date: String): String {
        return formatter.format(parser.parse(date)!!)
    }
}