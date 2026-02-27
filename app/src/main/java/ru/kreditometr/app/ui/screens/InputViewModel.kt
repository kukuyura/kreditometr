package ru.kreditometr.app.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.kreditometr.app.domain.LoanCalculator
import ru.kreditometr.app.domain.LoanParameters
import ru.kreditometr.app.domain.ProductType
import ru.kreditometr.app.util.ValidationResult
import ru.kreditometr.app.util.validateAmount
import ru.kreditometr.app.util.validateDownPayment
import ru.kreditometr.app.util.validateRate
import ru.kreditometr.app.util.validateTerm

data class InputUiState(
    val productType: ProductType = ProductType.CREDIT,
    val amountText: String = "",
    val termText: String = "",
    val termInYears: Boolean = false,
    val rateText: String = "",
    val downPaymentText: String = "",
    val amountError: String? = null,
    val termError: String? = null,
    val rateError: String? = null,
    val downPaymentError: String? = null,
    val showHighRateWarning: Boolean = false
) {
    fun canCalculate(): Boolean =
        amountText.isNotBlank() && termText.isNotBlank() &&
            (productType != ProductType.INSTALLMENT && rateText.isNotBlank() || productType == ProductType.INSTALLMENT) &&
            (productType != ProductType.MORTGAGE || downPaymentText.isNotBlank() || true)
}

class InputViewModel : ViewModel() {

    private val _state = MutableStateFlow(InputUiState())
    val state: StateFlow<InputUiState> = _state.asStateFlow()

    init {
        applyProductDefaults(ProductType.CREDIT)
    }

    fun setProductType(type: ProductType) {
        _state.update { it.copy(productType = type) }
        applyProductDefaults(type)
    }

    private fun applyProductDefaults(type: ProductType) {
        _state.update { state ->
            state.copy(
                rateText = when (type) {
                    ProductType.CREDIT -> "15"
                    ProductType.LOAN -> "0.8"
                    ProductType.MORTGAGE -> "9"
                    ProductType.INSTALLMENT -> "0"
                    ProductType.EDUCATION -> "3"
                },
                showHighRateWarning = type == ProductType.LOAN,
                rateError = null
            )
        }
    }

    fun setAmount(text: String) {
        _state.update {
            it.copy(amountText = text, amountError = null)
        }
    }

    fun setTerm(text: String) {
        _state.update {
            it.copy(termText = text, termError = null)
        }
    }

    fun setTermInYears(value: Boolean) {
        _state.update { it.copy(termInYears = value) }
    }

    fun setRate(text: String) {
        val type = _state.value.productType
        if (type == ProductType.EDUCATION || type == ProductType.INSTALLMENT) return
        _state.update {
            it.copy(
                rateText = text,
                rateError = null,
                showHighRateWarning = it.productType == ProductType.LOAN
            )
        }
    }

    fun setDownPayment(text: String) {
        _state.update {
            it.copy(downPaymentText = text, downPaymentError = null)
        }
    }

    /** Строит [LoanParameters] и выполняет расчёт. Возвращает true, если валидация прошла и расчёт выполнен. */
    fun validateAndCalculate(): LoanParameters? {
        val s = _state.value
        var ok = true

        val amountResult = validateAmount(s.amountText)
        _state.update {
            it.copy(amountError = (amountResult as? ValidationResult.Error)?.message)
        }
        if (amountResult is ValidationResult.Error) ok = false
        val amount = (amountResult as? ValidationResult.Ok)?.value ?: 0.0

        val termResult = validateTerm(s.termText)
        _state.update {
            it.copy(termError = (termResult as? ValidationResult.Error)?.message)
        }
        if (termResult is ValidationResult.Error) ok = false
        var termMonths = (termResult as? ValidationResult.Ok)?.value ?: 0
        if (s.termInYears) termMonths *= 12
        if (termMonths > 600) {
            _state.update { it.copy(termError = "Срок слишком большой") }
            ok = false
        }

        var rateError: String? = null
        val annualRate = when (s.productType) {
            ProductType.INSTALLMENT -> 0.0
            ProductType.EDUCATION -> 3.0
            else -> {
                val rateResult = validateRate(s.rateText)
                rateError = (rateResult as? ValidationResult.Error)?.message
                if (rateResult is ValidationResult.Error) ok = false
                when (s.productType) {
                    ProductType.LOAN -> LoanCalculator.dailyRateToAnnual((rateResult as? ValidationResult.Ok)?.value ?: 0.0)
                    else -> (rateResult as? ValidationResult.Ok)?.value ?: 0.0
                }
            }
        }

        _state.update { it.copy(rateError = rateError) }

        var downPayment = 0.0
        if (s.productType == ProductType.MORTGAGE) {
            val dpResult = validateDownPayment(s.downPaymentText, amount)
            _state.update {
                it.copy(downPaymentError = (dpResult as? ValidationResult.Error)?.message)
            }
            if (dpResult is ValidationResult.Error) ok = false
            downPayment = (dpResult as? ValidationResult.Ok)?.value ?: 0.0
        }

        if (!ok) return null

        val params = LoanParameters(
            productType = s.productType,
            amount = amount,
            termMonths = termMonths,
            annualRate = annualRate,
            downPayment = downPayment
        )
        return params
    }
}
