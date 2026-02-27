package ru.kreditometr.app.ui.state

import androidx.lifecycle.ViewModel
import ru.kreditometr.app.domain.LoanCalculationResult
import ru.kreditometr.app.domain.LoanParameters

/**
 * Состояние расчёта, общее для экранов ввода и результата.
 * После расчёта сохраняются параметры и результат для отображения на экране результата.
 */
class SharedCalculationState : ViewModel() {
    var lastParameters: LoanParameters? = null
        private set
    var lastResult: LoanCalculationResult? = null
        private set

    fun setResult(parameters: LoanParameters, result: LoanCalculationResult) {
        lastParameters = parameters
        lastResult = result
    }
}
