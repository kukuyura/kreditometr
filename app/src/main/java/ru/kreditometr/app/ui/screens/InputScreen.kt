package ru.kreditometr.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.kreditometr.app.R
import ru.kreditometr.app.domain.LoanCalculator
import ru.kreditometr.app.domain.ProductType
import ru.kreditometr.app.ui.state.SharedCalculationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    sharedState: SharedCalculationState,
    onNavigateToResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: InputViewModel = viewModel()
    val state by vm.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var productMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.product_type),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Box {
                OutlinedTextField(
                    value = productTypeLabel(state.productType),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { productMenuExpanded = true },
                    trailingIcon = {
                        IconButton(onClick = { productMenuExpanded = !productMenuExpanded }) {
                            Text("▼", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                )
                DropdownMenu(
                    expanded = productMenuExpanded,
                    onDismissRequest = { productMenuExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    ProductType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(productTypeLabel(type)) },
                            onClick = {
                                vm.setProductType(type)
                                productMenuExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.amountText,
                onValueChange = { vm.setAmount(formatNumberInput(it)) },
                label = { Text(stringResource(R.string.field_amount)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.amountError != null,
                supportingText = state.amountError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text(
                stringResource(R.string.field_term),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    selected = !state.termInYears,
                    onClick = { vm.setTermInYears(false) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    label = { Text(stringResource(R.string.field_term_months)) }
                )
                SegmentedButton(
                    selected = state.termInYears,
                    onClick = { vm.setTermInYears(true) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    label = { Text(stringResource(R.string.field_term_years)) }
                )
            }
            OutlinedTextField(
                value = state.termText,
                onValueChange = { vm.setTerm(it.filter { c -> c.isDigit() }) },
                label = { Text(if (state.termInYears) stringResource(R.string.field_term_years) else stringResource(R.string.field_term_months)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.termError != null,
                supportingText = state.termError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            val isRateLocked = state.productType == ProductType.INSTALLMENT || state.productType == ProductType.EDUCATION
            OutlinedTextField(
                value = state.rateText,
                onValueChange = { vm.setRate(it.filter { c -> c.isDigit() || c == '.' || c == ',' }) },
                label = {
                    Text(
                        if (state.productType == ProductType.LOAN)
                            stringResource(R.string.field_rate_per_day)
                        else
                            stringResource(R.string.field_rate)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isRateLocked,
                readOnly = isRateLocked,
                isError = state.rateError != null,
                supportingText = state.rateError?.let { { Text(it) } }
                    ?: if (state.showHighRateWarning) { { Text(stringResource(R.string.warning_high_rate), color = MaterialTheme.colorScheme.error) } } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            if (state.productType == ProductType.MORTGAGE) {
                OutlinedTextField(
                    value = state.downPaymentText,
                    onValueChange = { vm.setDownPayment(formatNumberInput(it)) },
                    label = { Text(stringResource(R.string.field_down_payment)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.downPaymentError != null,
                    supportingText = state.downPaymentError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    val params = vm.validateAndCalculate()
                    if (params != null) {
                        val result = LoanCalculator.calculate(params)
                        if (result != null) {
                            sharedState.setResult(params, result)
                            onNavigateToResult()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(stringResource(R.string.btn_calculate))
            }
        }
    }
}

private fun productTypeLabel(type: ProductType): String = when (type) {
    ProductType.CREDIT -> "Кредит"
    ProductType.LOAN -> "Займ"
    ProductType.MORTGAGE -> "Ипотека"
    ProductType.INSTALLMENT -> "Рассрочка"
    ProductType.EDUCATION -> "Образовательный кредит"
}

private fun formatNumberInput(s: String): String {
    val digits = s.filter { it.isDigit() }
    if (digits.isEmpty()) return ""
    return digits.reversed().chunked(3).joinToString("\u00A0").reversed()
}
