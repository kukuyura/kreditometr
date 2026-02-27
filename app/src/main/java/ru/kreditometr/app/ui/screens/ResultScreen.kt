package ru.kreditometr.app.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.kreditometr.app.R
import ru.kreditometr.app.data.formatCurrency
import ru.kreditometr.app.data.formatPaymentDate
import ru.kreditometr.app.data.formatPercent
import ru.kreditometr.app.domain.ProductType
import ru.kreditometr.app.ui.state.SharedCalculationState

private const val PREVIEW_SCHEDULE_ROWS = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    sharedState: SharedCalculationState,
    onBack: () -> Unit,
    onShowFullSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    val params = sharedState.lastParameters
    val result = sharedState.lastResult

    if (params == null || result == null) {
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.no_calculation_data))
            Button(onClick = onBack) { Text(stringResource(R.string.btn_back)) }
        }
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
        ,
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Text(
                    text = stringResource(R.string.calculator_disclaimer),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.result_summary),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("${stringResource(R.string.result_summary_type)}: ${productTypeLabel(params.productType)}")
                        Text("${stringResource(R.string.result_summary_amount)}: ${formatCurrency(params.amount)}")
                        if (params.downPayment > 0) {
                            Text("${stringResource(R.string.result_summary_down)}: ${formatCurrency(params.downPayment)}")
                            Text("${stringResource(R.string.result_summary_loan_amount)}: ${formatCurrency(params.loanAmount)}")
                        }
                        Text("${stringResource(R.string.result_summary_term)}: ${params.termMonths} мес.")
                        Text("${stringResource(R.string.result_summary_rate)}: ${formatPercent(params.annualRate)}")
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.result_monthly_payment),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            formatCurrency(result.monthlyPayment),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.result_total_payment))
                            Text(formatCurrency(result.totalPayment), fontWeight = FontWeight.SemiBold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.result_overpayment))
                            Text(formatCurrency(result.overpayment), fontWeight = FontWeight.SemiBold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.result_psk))
                            Text(formatPercent(result.aprPsk), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            item {
                Text(
                    stringResource(R.string.schedule_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.schedule_no), style = MaterialTheme.typography.labelSmall)
                    Text(stringResource(R.string.schedule_date), style = MaterialTheme.typography.labelSmall)
                    Text(stringResource(R.string.schedule_payment), style = MaterialTheme.typography.labelSmall)
                    Text(stringResource(R.string.schedule_remaining), style = MaterialTheme.typography.labelSmall)
                }
            }

            val previewList = result.payments.take(PREVIEW_SCHEDULE_ROWS)
            items(previewList) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(item.index.toString(), style = MaterialTheme.typography.bodySmall)
                    Text(formatPaymentDate(item.date), style = MaterialTheme.typography.bodySmall)
                    Text(formatCurrency(item.payment), style = MaterialTheme.typography.bodySmall)
                    Text(formatCurrency(item.remainingPrincipal), style = MaterialTheme.typography.bodySmall)
                }
            }

            if (result.payments.size > PREVIEW_SCHEDULE_ROWS) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onShowFullSchedule,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.btn_show_full_schedule))
                    }
                }
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
