package ru.kreditometr.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.YearMonth

/**
 * Unit-тесты для [LoanCalculator].
 */
class LoanCalculatorTest {

    @Test
    fun `calculate returns null for zero amount`() {
        val params = LoanParameters(
            productType = ProductType.CREDIT,
            amount = 0.0,
            termMonths = 12,
            annualRate = 12.0
        )
        assertNull(LoanCalculator.calculate(params))
    }

    @Test
    fun `calculate returns null for zero term`() {
        val params = LoanParameters(
            productType = ProductType.CREDIT,
            amount = 100_000.0,
            termMonths = 0,
            annualRate = 12.0
        )
        assertNull(LoanCalculator.calculate(params))
    }

    @Test
    fun `annuity monthly payment formula`() {
        val params = LoanParameters(
            productType = ProductType.CREDIT,
            amount = 100_000.0,
            termMonths = 12,
            annualRate = 12.0
        )
        val result = LoanCalculator.calculate(params)
        assertNotNull(result)
        // Платеж = 100000 * (0.01 * (1.01)^12) / ((1.01)^12 - 1) ≈ 8884.88
        assertEquals(8884.88, result!!.monthlyPayment, 0.5)
        assertEquals(12, result.payments.size)
        assertEquals(0.0, result.payments.last().remainingPrincipal, 0.01)
    }

    @Test
    fun `overpayment and total payment`() {
        val params = LoanParameters(
            productType = ProductType.CREDIT,
            amount = 100_000.0,
            termMonths = 12,
            annualRate = 12.0
        )
        val result = LoanCalculator.calculate(params)!!
        val expectedTotal = result.monthlyPayment * 12
        assertEquals(expectedTotal, result.totalPayment, 0.01)
        assertEquals(expectedTotal - 100_000.0, result.overpayment, 0.5)
    }

    @Test
    fun `installment zero rate`() {
        val params = LoanParameters(
            productType = ProductType.INSTALLMENT,
            amount = 60_000.0,
            termMonths = 12,
            annualRate = 0.0
        )
        val result = LoanCalculator.calculate(params)
        assertNotNull(result)
        assertEquals(5_000.0, result!!.monthlyPayment, 0.01)
        assertEquals(60_000.0, result.totalPayment, 0.01)
        assertEquals(0.0, result.overpayment, 0.01)
        assertEquals(0.0, result.aprPsk, 0.01)
        assertEquals(12, result.payments.size)
    }

    @Test
    fun `mortgage with down payment`() {
        val params = LoanParameters(
            productType = ProductType.MORTGAGE,
            amount = 5_000_000.0,
            termMonths = 240,
            annualRate = 9.0,
            downPayment = 1_000_000.0
        )
        val result = LoanCalculator.calculate(params)
        assertNotNull(result)
        assertEquals(4_000_000.0, params.loanAmount, 0.01)
        assertEquals(240, result!!.payments.size)
        assertEquals(0.0, result.payments.last().remainingPrincipal, 1.0)
    }

    @Test
    fun `daily rate to annual`() {
        assertEquals(292.0, LoanCalculator.dailyRateToAnnual(0.8), 0.01)
        assertEquals(0.0, LoanCalculator.dailyRateToAnnual(0.0), 0.01)
    }

    @Test
    fun `loan with daily rate converted to annual`() {
        val params = LoanParameters(
            productType = ProductType.LOAN,
            amount = 10_000.0,
            termMonths = 6,
            annualRate = LoanCalculator.dailyRateToAnnual(0.8)
        )
        val result = LoanCalculator.calculate(params)
        assertNotNull(result)
        assert(result!!.overpayment > 0)
        assert(result.aprPsk > 0)
    }

    @Test
    fun `schedule dates use firstPaymentMonth`() {
        val first = YearMonth.of(2025, 3)
        val params = LoanParameters(
            productType = ProductType.CREDIT,
            amount = 12_000.0,
            termMonths = 3,
            annualRate = 0.0
        )
        val result = LoanCalculator.calculate(params, firstPaymentMonth = first)
        assertNotNull(result)
        assertEquals(YearMonth.of(2025, 3), result!!.payments[0].date)
        assertEquals(YearMonth.of(2025, 4), result.payments[1].date)
        assertEquals(YearMonth.of(2025, 5), result.payments[2].date)
    }
}
