package com.openclassrooms.rebonnte.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.openclassrooms.rebonnte.ui.utils.Format
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

class FormatTest {

    @Test
    fun `getEnglishOrdinalSuffix returns correct suffix`() {
        assertThat(Format.getEnglishOrdinalSuffix(1)).isEqualTo("st")
        assertThat(Format.getEnglishOrdinalSuffix(2)).isEqualTo("nd")
        assertThat(Format.getEnglishOrdinalSuffix(3)).isEqualTo("rd")
        assertThat(Format.getEnglishOrdinalSuffix(4)).isEqualTo("th")

        assertThat(Format.getEnglishOrdinalSuffix(11)).isEqualTo("th")
        assertThat(Format.getEnglishOrdinalSuffix(12)).isEqualTo("th")
        assertThat(Format.getEnglishOrdinalSuffix(13)).isEqualTo("th")

        assertThat(Format.getEnglishOrdinalSuffix(21)).isEqualTo("st")
        assertThat(Format.getEnglishOrdinalSuffix(22)).isEqualTo("nd")
        assertThat(Format.getEnglishOrdinalSuffix(23)).isEqualTo("rd")
    }

    @Test
    fun `getLocalizedDateParts formats french date and time`() {
        val instant = Instant.parse("2024-03-05T14:07:00Z")

        val (date, time) = Format.getLocalizedDateParts(
            instant = instant,
            locale = Locale.FRENCH,
            zoneId = ZoneId.of("Europe/Paris")
        )

        assertThat(date).isEqualTo("5 mars 2024")
        assertThat(time).isEqualTo("15h07")
    }

    @Test
    fun `getShortLocalizedDate formats english date`() {
        val instant = Instant.parse("2024-12-09T00:00:00Z")

        val result = Format.getShortLocalizedDate(
            instant = instant,
            locale = Locale.US,
            zoneId = ZoneId.of("UTC")
        )

        assertThat(result).isEqualTo("12/09/24")
    }
}