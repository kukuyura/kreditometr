package ru.kreditometr.app.domain

/**
 * Тип кредитного продукта.
 */
enum class ProductType {
    /** Потребительский кредит */
    CREDIT,

    /** Микрозайм */
    LOAN,

    /** Ипотека */
    MORTGAGE,

    /** Товарная рассрочка */
    INSTALLMENT,

    /** Образовательный кредит */
    EDUCATION
}
