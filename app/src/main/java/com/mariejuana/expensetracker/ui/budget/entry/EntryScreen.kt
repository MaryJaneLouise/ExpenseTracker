package com.mariejuana.expensetracker.ui.budget.entry

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.mariejuana.expensetracker.data.expense.Budget
import com.mariejuana.expensetracker.ui.navigation.NavigationDestination
import com.mariejuana.expensetracker.ui.theme.ExpenseTrackerTheme

object BudgetEntryDestination : NavigationDestination {
    override val route = "budget_entry"
    override val titleRes = R.string.budget_add_button
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BudgetEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: BudgetEntryScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val currentBudget by viewModel.currentBudget.collectAsState()

    val context = LocalContext.current

    Scaffold (
        topBar = {
            ExpenseTopAppBar(
                title = stringResource(BudgetEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
            )
        }
    ) {  innerPadding ->
            BudgetEntryBody(
                navigateBack = navigateBack,
                budgetUiState = viewModel.budgetUiState,
                onItemValueChange = viewModel::updateUiState,
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            )
        }
}

@Composable
fun BudgetEntryBody (
    navigateBack: () -> Unit,
    budgetUiState: BudgetUiState,
    onItemValueChange: (BudgetDetails, TransactionDetails) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BudgetEntryScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentBudget by viewModel.currentBudget.collectAsState()

    var insertConfirmationRequired by rememberSaveable { mutableStateOf(false) }

    Column (
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        BudgetInputForm (
            budgetDetails = budgetUiState.budgetDetails,
            transactionDetails = budgetUiState.transactionDetails,
            onValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        FilledTonalButton(
            onClick = {
               if (viewModel.loadSettingsForceInsert(context)) {
                   insertConfirmationRequired = false

                   val newAmount = viewModel.budgetUiState.budgetDetails.amount.toDouble()
                   if (currentBudget?.amount != null) {
                       coroutineScope.launch {
                           viewModel.addAndUpdateBudget(newAmount)
                           navigateBack()
                           Toast.makeText(context, "Budget added successfully.", Toast.LENGTH_LONG).show()
                       }
                   } else {
                       coroutineScope.launch {
                           viewModel.saveBudget()
                           navigateBack()
                           Toast.makeText(context, "Budget added successfully.", Toast.LENGTH_LONG).show()
                       }
                   }
               } else {
                   insertConfirmationRequired = true
               }
            },
            enabled = budgetUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row (
                modifier = Modifier.padding(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Save,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.expense_entry_save),
                    style = MaterialTheme.typography.bodyLarge)
            }
        }

        if (insertConfirmationRequired) {
            InsertConfirmationDialog(
                onInsertConfirm = {
                    insertConfirmationRequired = false

                    val newAmount = viewModel.budgetUiState.budgetDetails.amount.toDouble()
                    if (currentBudget?.amount != null) {
                        coroutineScope.launch {
                            viewModel.addAndUpdateBudget(newAmount)
                            navigateBack()
                            Toast.makeText(context, "Budget added successfully.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        coroutineScope.launch {
                            viewModel.saveBudget()
                            navigateBack()
                            Toast.makeText(context, "Budget added successfully.", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                onInsertCancel = { insertConfirmationRequired = false })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetInputForm(
    budgetDetails: BudgetDetails,
    transactionDetails: TransactionDetails,
    modifier: Modifier = Modifier,
    onValueChange: (BudgetDetails, TransactionDetails) -> Unit = { _, _ -> },
    enabled: Boolean = true
) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = budgetDetails.amount,
            onValueChange = {
                val double = it.toDoubleOrNull()
                if((double != null && double > 0) || it == "") {
                    onValueChange(budgetDetails.copy(amount = it), transactionDetails.copy(amount = it))
                }},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            label = { Text(stringResource(R.string.budget_title)) },
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

@Composable
private fun InsertConfirmationDialog(
    onInsertConfirm: () -> Unit,
    onInsertCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.insert_budget_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onInsertCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onInsertConfirm) {
                Text(text = stringResource(R.string.yes))
            }
        })
}