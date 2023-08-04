package com.voiceapprovel.mobile.utilty

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Ajay Vamsee on 8/3/2023.
 * Time : 13:50
 */

object DateTimeUtils {

    private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MMM dd',' yyyy", Locale.getDefault())

    suspend fun formatDateTime(dateTimeString: String): Pair<String, String> {
        return withContext(Dispatchers.Default) {
            val dateTime = dateTimeFormatter.parse(dateTimeString)
            val time = timeFormat.format(dateTime)
            val date = dateFormat.format(dateTime)
            Pair(time, date)
        }
    }
}