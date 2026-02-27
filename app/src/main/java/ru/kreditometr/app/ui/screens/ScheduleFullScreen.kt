package ru.kreditometr.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.kreditometr.app.R
import ru.kreditometr.app.data.formatCurrency
import ru.kreditometr.app.data.formatPaymentDate
import ru.kreditometr.app.domain.PaymentItem
import ru.kreditometr.app.ui.state.SharedCalculationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleFullScreen(
    sharedState: SharedCalculationState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = sharedState.lastResult

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.schedule_title)) },
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
        if (result == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.no_schedule_data))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val rowScroll = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rowScroll)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.schedule_no), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Text(stringResource(R.string.schedule_date), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Text(stringResource(R.string.schedule_payment), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Text(stringResource(R.string.schedule_interest), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Text(stringResource(R.string.schedule_principal), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Text(stringResource(R.string.schedule_remaining), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(
                    items = result.payments,
                    key = { it.index }
                ) { item: PaymentItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.index.toString(), style = MaterialTheme.typography.bodySmall)
                        Text(formatPaymentDate(item.date), style = MaterialTheme.typography.bodySmall)
                        Text(formatCurrency(item.payment), style = MaterialTheme.typography.bodySmall)
                        Text(formatCurrency(item.interestPart), style = MaterialTheme.typography.bodySmall)
                        Text(formatCurrency(item.principalPart), style = MaterialTheme.typography.bodySmall)
                        Text(formatCurrency(item.remainingPrincipal), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
