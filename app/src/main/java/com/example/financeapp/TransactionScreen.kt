@file:Suppress("FunctionSignature")

package com.example.financeapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
@Suppress("FunctionNaming")
internal fun TransactionScreen(
    viewModel: TransactionViewModel,
    showBalance: Boolean,
    onOpenSettings: () -> Unit,
) {
    val state = viewModel.uiState
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Text(text = stringResource(id = R.string.transaction_title))
            TextButton(onClick = onOpenSettings) {
                Text(stringResource(R.string.settings_title))
            }
            if (showBalance) {
                Text(stringResource(R.string.balance_summary, state.balance, state.transactionCount))
            } else {
                Text(stringResource(R.string.transaction_count, state.transactionCount))
            }
            if (state.isLoading) {
                Text(text = stringResource(id = R.string.transaction_loading))
            }
            TransactionForm(viewModel, state)
            if (state.transactions.isNotEmpty()) {
                Text(text = stringResource(id = R.string.transaction_history))
                state.transactions.asReversed().forEach { tx ->
                    val row = if (tx.type == TransactionType.INCOME) {
                        R.string.transaction_history_income
                    } else {
                        R.string.transaction_history_expense
                    }
                    Text(text = stringResource(id = row, tx.description, tx.amount))
                }
            }
        }
    }
}

@Composable
@Suppress("FunctionNaming")
private fun TransactionForm(viewModel: TransactionViewModel, state: MainUiState) {
    val isFormEnabled = state.isDatabaseReady && !state.isSaving

    OutlinedTextField(
        value = state.description,
        onValueChange = { viewModel.onEvent(MainUiEvent.DescriptionChanged(it)) },
        enabled = isFormEnabled,
        label = { Text(text = stringResource(id = R.string.transaction_description)) },
    )
    OutlinedTextField(
        value = state.amount,
        onValueChange = { viewModel.onEvent(MainUiEvent.AmountChanged(it)) },
        enabled = isFormEnabled,
        label = { Text(text = stringResource(id = R.string.transaction_amount)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
    Row {
        FilterChip(
            selected = state.type == TransactionType.EXPENSE,
            onClick = { viewModel.onEvent(MainUiEvent.TypeChanged(TransactionType.EXPENSE)) },
            enabled = isFormEnabled,
            label = { Text(text = stringResource(id = R.string.transaction_expense)) },
        )
        FilterChip(
            selected = state.type == TransactionType.INCOME,
            onClick = { viewModel.onEvent(MainUiEvent.TypeChanged(TransactionType.INCOME)) },
            enabled = isFormEnabled,
            label = { Text(text = stringResource(id = R.string.transaction_income)) },
        )
    }
    state.error?.let { error ->
        val textId = when (error) {
            TransactionError.INVALID_INPUT -> R.string.transaction_invalid_input
            TransactionError.AMOUNT_TOO_LARGE -> R.string.transaction_amount_too_large
            TransactionError.DATABASE_ERROR -> R.string.transaction_database_error
        }
        Text(
            text = stringResource(id = textId),
            color = MaterialTheme.colorScheme.error,
        )
    }
    Button(
        onClick = viewModel::addTransaction,
        enabled = isFormEnabled,
    ) {
        Text(text = stringResource(id = R.string.transaction_add))
    }
}
