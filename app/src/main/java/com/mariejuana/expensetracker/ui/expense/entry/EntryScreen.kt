package com.mariejuana.expensetracker.ui.expense.entry

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Currency
import java.util.Date
import java.util.Locale
import com.mariejuana.expensetracker.application.ExpenseTopAppBar
import com.mariejuana.expensetracker.R
import com.mariejuana.expensetracker.application.AppViewModelProvider
import com.mariejuana.expensetracker.ui.navigation.NavigationDestination
import com.mariejuana.expensetracker.ui.theme.ExpenseTrackerTheme
import java.text.NumberFormat

object ExpenseEntryDestination : NavigationDestination {
    override val route = "expense_entry"
    override val titleRes = R.string.expense_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun ExpenseEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: EntryScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val currentBudget by viewModel.currentBudget.collectAsState()
    var showSnackbar by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold (
        topBar = {
            ExpenseTopAppBar(
                title = stringResource(ExpenseEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
            )
        }
    ) {  innerPadding ->
        ExpenseEntryBody(
            expenseUiState = viewModel.expenseUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                val newAmount = viewModel.expenseUiState.expenseDetails.amount.toDouble()

                if ((currentBudget?.amount?.minus(newAmount))!! >= 0) {
                    coroutineScope.launch {
                        viewModel.saveExpense(newAmount)
                        navigateBack()
                    }
                }
                else {
                    Toast.makeText(context, "Please add more budget before adding expenses again.", Toast.LENGTH_LONG).show()
                }

            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )


    }
}

@Composable
fun ExpenseEntryBody (
    expenseUiState: ExpenseUiState,
    onItemValueChange: (ExpenseDetails, TransactionDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        ExpenseInputForm (
            expenseDetails = expenseUiState.expenseDetails,
            transactionDetails = expenseUiState.transactionDetails,
            onValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = expenseUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.expense_entry_save))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseInputForm(
    expenseDetails: ExpenseDetails,
    transactionDetails: TransactionDetails,
    modifier: Modifier = Modifier,
    onValueChange: (ExpenseDetails, TransactionDetails) -> Unit = { _, _ -> },
    enabled: Boolean = true
) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = expenseDetails.name,
            onValueChange = { onValueChange(expenseDetails.copy(name = it), transactionDetails.copy(name = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = { Text(stringResource(R.string.expense_entry_name)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = expenseDetails.type,
            onValueChange = { onValueChange(expenseDetails.copy(type = it), transactionDetails.copy(type = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = { Text(stringResource(R.string.expense_entry_type)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = expenseDetails.amount,
            onValueChange = { onValueChange(expenseDetails.copy(amount = it), transactionDetails.copy(amount = it))},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text(stringResource(R.string.expense_entry_amount)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            ),
            leadingIcon = { Text(Currency.getInstance(Locale.getDefault()).symbol) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
    }
}