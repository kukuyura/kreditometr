package ru.kreditometr.app.domain

import java.time.YearMonth

/**
 * Один платёж в графике.
 *
 * @param index номер платежа (1-based)
 * @param date месяц платежа
 * @param payment сумма платежа
 * @param interestPart сумма на погашение процентов
 * @param principalPart сумма на погашение основного долга
 * @param remainingPrincipal остаток долга после платежа
 */
data class PaymentItem(
    val index: Int,
    val date: YearMonth,
    val payment: Double,
    val interestPart: Double,
    val principalPart: Double,
    val remainingPrincipal: Double
)
