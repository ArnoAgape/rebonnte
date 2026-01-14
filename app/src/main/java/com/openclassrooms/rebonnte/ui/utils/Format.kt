package com.openclassrooms.rebonnte.ui.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries.localDate
import java.util.Locale

object Format {

    fun getLocalizedDateParts(
        instant: Instant,
        locale: Locale = Locale.getDefault(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Pair<String, String> {

        val zoned = instant.atZone(zoneId)
        val localDate = zoned.toLocalDate()
        val localTime = zoned.toLocalTime()

        return if (locale.language == "fr") {
            val date = DateTimeFormatter
                .ofPattern("d MMMM yyyy", locale)
                .format(localDate)

            val time = DateTimeFormatter
                .ofPattern("HH'h'mm", locale)
                .format(localTime)

            date to time
        } else {
            val day = localDate.dayOfMonth
            val suffix = getEnglishOrdinalSuffix(day)

            val date = DateTimeFormatter
                .ofPattern("MMMM d'$suffix' yyyy", locale)
                .format(localDate)

            val isAmPm = locale.country in listOf("US", "CA", "GB", "AU", "NZ")
            val timeFormatter =
                if (isAmPm)
                    DateTimeFormatter.ofPattern("h:mm a", locale)
                else
                    DateTimeFormatter.ofPattern("HH:mm", locale)

            val time = timeFormatter.format(localTime)

            date to time
        }
    }

    fun getShortLocalizedDate(
        instant: Instant,
        locale: Locale = Locale.getDefault(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): String {

        val zoned = instant.atZone(zoneId)
        val localDate = zoned.toLocalDate()

        return if (locale.language == "fr") {

            DateTimeFormatter
                .ofPattern("dd/MM/yy", locale)
                .format(localDate)

        } else {

            DateTimeFormatter
                .ofPattern("MM/dd/yy", locale)
                .format(localDate)
        }
    }

    fun getEnglishOrdinalSuffix(day: Int): String = when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
}