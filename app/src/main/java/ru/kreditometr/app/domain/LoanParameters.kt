package ru.kreditometr.app.domain

/**
 * Параметры кредита для расчёта.
 *
 * @param productType тип продукта
 * @param amount сумма кредита (руб.) — для ипотеки это стоимость недвижимости, сумма кредита = amount - downPayment
 * @param termMonths срок в месяцах
 * @param annualRate годовая процентная ставка (в процентах, например 15.0). Для займа может быть пересчитана из дневной.
 * @param downPayment первоначальный взнос (руб., только для ипотеки)
 */
data class LoanParameters(
    val productType: ProductType,
    val amount: Double,
    val termMonths: Int,
    val annualRate: Double,
    val downPayment: Double = 0.0
) {
    /** Сумма кредита с учётом первоначального взноса (для ипотеки). */
    val loanAmount: Double
        get() = (amount - downPayment).coerceAtLeast(0.0)
}
