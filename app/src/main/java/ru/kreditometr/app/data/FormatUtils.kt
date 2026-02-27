package ru.kreditometr.app.data

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ruLocale = Locale("ru", "RU")

/** Форматирование суммы в рублях с пробелами между разрядами (например, 1 234 567 ₽). */
fun formatCurrency(value: Double): String {
    val df = DecimalFormat("#,###", DecimalFormatSymbols(ruLocale).apply {
        groupingSeparator = '\u00A0' // non-breaking space
    })
    return "${df.format(value.toLong())} ₽"
}

/** Форматирование числа с пробелами (например, 12.5 для процентов). */
fun formatPercent(value: Double): String {
    val df = DecimalFormat("#,##0.##", DecimalFormatSymbols(ruLocale).apply {
        groupingSeparator = '\u00A0'
        decimalSeparator = '.'
    })
    return "${df.format(value)} %"
}

/** Формат даты графика платежей: MM.YYYY */
private val dateFormatter = DateTimeFormatter.ofPattern("MM.yyyy", ruLocale)

fun formatPaymentDate(yearMonth: YearMonth): String = yearMonth.format(dateFormatter)
