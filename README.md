# Кредитометр

Android MVP: financial calculator for consumer credit, microloans, mortgages, installments, and education loans.

Kotlin + Jetpack Compose app with a small domain layer for annuity math, payment schedules, and a simplified APR/ПСК estimate — plus unit tests for the calculator.

![App icon](icon.png)

## Features

- Product types: кредит, займ, ипотека, рассрочка, образовательный кредит
- Inputs: amount, term (months/years), rate; mortgage down payment
- Outputs: annuity payment, total paid, overpayment, ПСК
- Full payment schedule + share as a text report
- Loan product: daily rate → annual conversion for display

## Stack

| Layer | Tech |
|-------|------|
| UI | Jetpack Compose, Material 3, Navigation Compose, single-Activity |
| Domain | `LoanCalculator`, product models, schedule generation |
| Data / util | Formatting, input validation |
| Tests | JUnit on JVM (`LoanCalculatorTest`) |
| Tooling | Kotlin 1.9, AGP 8.x, JDK 17, minSdk 24 / targetSdk 35 |

### Architecture

```
ui/        Input / Result / Schedule screens, ViewModels, theme
domain/    ProductType, LoanParameters, LoanCalculator, PaymentItem
data/      FormatUtils
util/      Validation
```

Shared calculation result lives in `SharedCalculationState` (Activity-scoped ViewModel). Formulas follow the product spec: annuity payment, overpayment, simplified ПСК; installment is 0% rate; mortgage uses `amount − downPayment` as the loan principal.

## Requirements

- Android Studio (Ladybug 2024.2+ or any setup with Kotlin 1.9 / AGP 8.x)
- JDK 17
- Android SDK: minSdk 24, targetSdk 35, compileSdk 35

## Build & run

1. Open the repo root in Android Studio and sync Gradle.
2. Use a device/emulator with API 24+.
3. **Run → Run 'app'**, or:

```bash
./gradlew installDebug
./gradlew assembleDebug    # → app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease
```

## Tests

```bash
./gradlew testDebugUnitTest
```

Report: `app/build/reports/tests/testDebugUnitTest/index.html`.

Covered cases include null inputs, annuity payment ≈ known value, installment zeros, mortgage down payment, daily→annual rate, and schedule dates.

## Localization

UI strings are Russian (`res/values/strings.xml`). Extra locales can be added via `values-*`.

## Portfolio note

Solid **Android / Kotlin** MVP: clean domain vs UI split, Compose navigation, and tested loan math. Good supporting mobile project; not a full fintech product (no backend, no regulatory ПСК, MVP scope). Pin if targeting Android roles; otherwise keep as a secondary sample.

## License

Educational / personal project.
