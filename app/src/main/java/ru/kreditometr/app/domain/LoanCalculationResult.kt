package ru.kreditometr.app.domain

/**
 * Результат расчёта кредита.
 *
 * @param monthlyPayment ежемесячный платёж (руб.)
 * @param totalPayment общая сумма выплат (основной долг + проценты)
 * @param overpayment переплата (проценты)
 * @param aprPsk полная стоимость кредита (ПСК), % годовых
 * @param payments график платежей
 */
data class LoanCalculationResult(
    val monthlyPayment: Double,
    val totalPayment: Double,
    val overpayment: Double,
    val aprPsk: Double,
    val payments: List<PaymentItem>
)
