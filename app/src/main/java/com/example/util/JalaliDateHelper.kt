package com.example.util

import java.util.Calendar

object JalaliDateHelper {

    fun getJalaliDateFromTimestamp(timestamp: Long, useFaDigits: Boolean = true): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val gy = calendar.get(Calendar.YEAR)
        val gm = calendar.get(Calendar.MONTH) + 1
        val gd = calendar.get(Calendar.DAY_OF_MONTH)

        val jalali = gregorianToJalali(gy, gm, gd)
        
        val monthNamesFa = listOf(
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
        )
        val monthNamesEn = listOf(
            "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar",
            "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand"
        )

        val monthName = if (useFaDigits) monthNamesFa[jalali.month - 1] else monthNamesEn[jalali.month - 1]
        
        return if (useFaDigits) {
            val yStr = toPersianDigits(jalali.year.toString())
            val dStr = toPersianDigits(jalali.day.toString())
            "$dStr $monthName $yStr"
        } else {
            "${jalali.day} $monthName ${jalali.year}"
        }
    }

    fun toPersianDigits(number: String): String {
        val persianChars = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val builder = java.lang.StringBuilder()
        for (i in 0 until number.length) {
            val element = number[i]
            if (Character.isDigit(element)) {
                builder.append(persianChars[element - '0'])
            } else {
                builder.append(element)
            }
        }
        return builder.toString()
    }

    class JalaliDate(val year: Int, val month: Int, val day: Int)

    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): JalaliDate {
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        var gy2 = gy
        if (gy2 > 1600) {
            gy2 -= 1600
        }
        
        var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400
        for (i in 1 until gm) {
            var days = gDaysInMonth[i]
            if (i == 2 && ((gy2 % 4 == 0 && gy2 % 100 != 0) || (gy2 % 400 == 0))) {
                days++
            }
            gDayNo += days
        }
        gDayNo += gd - 1

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 1
        for (i in 1..12) {
            var days = jDaysInMonth[i]
            if (i == 12 && ((jy % 33) in listOf(1, 5, 9, 13, 17, 22, 26, 30))) {
                days++
            }
            if (jDayNo < days) {
                jm = i
                break
            }
            jDayNo -= days
        }
        val jd = jDayNo + 1
        return JalaliDate(jy, jm, jd)
    }
}
