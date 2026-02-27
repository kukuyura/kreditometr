package ru.kreditometr.app.util

/** Результат валидации поля: либо значение, либо сообщение об ошибке. */
sealed class ValidationResult<out T> {
    data class Ok<T>(val value: T) : ValidationResult<T>()
    data class Error(val message: String) : ValidationResult<Nothing>()
}

private const val MAX_AMOUNT = 1_000_000_000_000.0
private const val MAX_TERM_MONTHS = 600
private const val MAX_RATE = 1000.0

fun validateAmount(raw: String): ValidationResult<Double> {
    if (raw.isBlank()) return ValidationResult.Error("Введите сумму")
    val cleaned = raw.replace("\u00A0", "").replace(" ", "").replace(",", ".")
    val value = cleaned.toDoubleOrNull() ?: return ValidationResult.Error("Некорректное число")
    if (value <= 0) return ValidationResult.Error("Сумма должна быть больше 0")
    if (value > MAX_AMOUNT) return ValidationResult.Error("Слишком большая сумма")
    return ValidationResult.Ok(value)
}

fun validateTerm(raw: String): ValidationResult<Int> {
    if (raw.isBlank()) return ValidationResult.Error("Введите срок")
    val value = raw.replace("\u00A0", "").replace(" ", "").toIntOrNull()
        ?: return ValidationResult.Error("Некорректное число")
    if (value <= 0) return ValidationResult.Error("Срок должен быть больше 0")
    if (value > MAX_TERM_MONTHS) return ValidationResult.Error("Срок слишком большой")
    return ValidationResult.Ok(value)
}

fun validateRate(raw: String): ValidationResult<Double> {
    if (raw.isBlank()) return ValidationResult.Error("Введите ставку")
    val value = raw.replace(",", ".").toDoubleOrNull()
        ?: return ValidationResult.Error("Некорректное число")
    if (value < 0) return ValidationResult.Error("Ставка не может быть отрицательной")
    if (value > MAX_RATE) return ValidationResult.Error("Слишком большая ставка")
    return ValidationResult.Ok(value)
}

fun validateDownPayment(raw: String, maxAmount: Double): ValidationResult<Double> {
    if (raw.isBlank()) return ValidationResult.Ok(0.0)
    val cleaned = raw.replace("\u00A0", "").replace(" ", "").replace(",", ".")
    val value = cleaned.toDoubleOrNull() ?: return ValidationResult.Error("Некорректное число")
    if (value < 0) return ValidationResult.Error("Взнос не может быть отрицательным")
    if (value > maxAmount) return ValidationResult.Error("Взнос не может превышать сумму")
    return ValidationResult.Ok(value)
}
