package ru.kreditometr.app.domain

import java.time.YearMonth
import kotlin.math.pow

/**
 * Сервис расчёта параметров кредита (аннуитетная схема).
 *
 * Формулы по ТЗ:
 * - Аннуитетный платёж: Платеж = Сумма * (i * (1 + i)^n) / ((1 + i)^n - 1)
 * - Переплата = (Платеж * n) - Сумма
 * - ПСК = (Переплата / Сумма) / (n / 12) * 100
 */
object LoanCalculator {

    /**
     * Рассчитывает результат по аннуитетной схеме с учётом типа продукта.
     *
     * @param params параметры кредита
     * @param firstPaymentMonth первый месяц платежа (для дат в графике)
     * @return результат расчёта или null при некорректных данных (сумма/срок <= 0)
     */
    fun calculate(
        params: LoanParameters,
        firstPaymentMonth: YearMonth = YearMonth.now().plusMonths(1)
    ): LoanCalculationResult? {
        val sum = params.loanAmount
        val n = params.termMonths
        if (sum <= 0 || n <= 0) return null

        return when (params.productType) {
            ProductType.INSTALLMENT -> calculateInstallment(sum, n, firstPaymentMonth)
            else -> calculateAnnuity(
                sum = sum,
                termMonths = n,
                annualRate = params.annualRate,
                firstPaymentMonth = firstPaymentMonth
            )
        }
    }

    /**
     * Рассрочка: ставка 0%, платёж = сумма / срок, переплата = 0, ПСК = 0.
     */
    private fun calculateInstallment(
        sum: Double,
        termMonths: Int,
        firstPaymentMonth: YearMonth
    ): LoanCalculationResult {
        val payment = sum / termMonths
        val payments = (1..termMonths).map { k ->
            val date = firstPaymentMonth.plusMonths((k - 1).toLong())
            val remaining = (sum - payment * k).coerceAtLeast(0.0)
            PaymentItem(
                index = k,
                date = date,
                payment = payment,
                interestPart = 0.0,
                principalPart = payment,
                remainingPrincipal = remaining
            )
        }
        return LoanCalculationResult(
            monthlyPayment = payment,
            totalPayment = sum,
            overpayment = 0.0,
            aprPsk = 0.0,
            payments = payments
        )
    }

    /**
     * Аннуитетный расчёт.
     * i = годовая ставка / 12 / 100.
     * Платеж = Сумма * (i * (1+i)^n) / ((1+i)^n - 1)
     */
    private fun calculateAnnuity(
        sum: Double,
        termMonths: Int,
        annualRate: Double,
        firstPaymentMonth: YearMonth
    ): LoanCalculationResult? {
        val i = annualRate / 12.0 / 100.0
        val n = termMonths.toDouble()
        val factor = (1.0 + i).pow(n)
        if (factor <= 1.0) {
            // ставка 0 или отрицательная — считаем как рассрочку
            return calculateInstallment(sum, termMonths, firstPaymentMonth)
        }
        val payment = sum * (i * factor) / (factor - 1.0)
        val totalPayment = payment * termMonths
        val overpayment = totalPayment - sum
        val aprPsk = if (sum > 0 && termMonths > 0) {
            (overpayment / sum) / (termMonths / 12.0) * 100.0
        } else 0.0

        var remaining = sum
        val payments = (1..termMonths).map { k ->
            val interestPart = remaining * i
            val principalPart = payment - interestPart
            remaining = (remaining - principalPart).coerceAtLeast(0.0)
            val date = firstPaymentMonth.plusMonths((k - 1).toLong())
            PaymentItem(
                index = k,
                date = date,
                payment = payment,
                interestPart = interestPart,
                principalPart = principalPart,
                remainingPrincipal = remaining
            )
        }

        return LoanCalculationResult(
            monthlyPayment = payment,
            totalPayment = totalPayment,
            overpayment = overpayment,
            aprPsk = aprPsk,
            payments = payments
        )
    }

    /**
     * Пересчёт дневной ставки в годовую для отображения ПСК (займ): годовая = дневная * 365.
     */
    fun dailyRateToAnnual(dailyRatePercent: Double): Double = dailyRatePercent * 365.0
}
