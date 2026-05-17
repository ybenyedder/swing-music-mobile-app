package com.android.swingmusic.core.domain.util

fun relativeAgo(epochSec: Double?): String {
    if (epochSec == null || epochSec <= 0) return ""
    val nowSec = System.currentTimeMillis() / 1000
    val deltaSec = nowSec - epochSec.toLong()
    if (deltaSec < 0) return "JUST NOW"
    return when {
        deltaSec < 60 -> "JUST NOW"
        deltaSec < 3600 -> "${deltaSec / 60} MIN AGO"
        deltaSec < 86_400 -> "${deltaSec / 3600} HRS AGO"
        deltaSec < 7 * 86_400 -> {
            val d = deltaSec / 86_400
            if (d <= 1L) "1 DAY AGO" else "$d DAYS AGO"
        }
        deltaSec < 30 * 86_400 -> {
            val w = deltaSec / (7 * 86_400)
            if (w <= 1L) "1 WEEK AGO" else "$w WEEKS AGO"
        }
        deltaSec < 365 * 86_400 -> {
            val m = deltaSec / (30 * 86_400)
            if (m <= 1L) "1 MONTH AGO" else "$m MONTHS AGO"
        }
        else -> {
            val y = deltaSec / (365 * 86_400)
            if (y <= 1L) "1 YEAR AGO" else "$y YEARS AGO"
        }
    }
}
