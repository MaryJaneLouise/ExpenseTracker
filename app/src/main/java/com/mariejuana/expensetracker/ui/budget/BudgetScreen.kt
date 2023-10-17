package com.mariejuana.expensetracker.ui.budget

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale
import com.mariejuana.expensetracker.application.ExpenseTopAppBar
import com.mariejuana.expensetracker.R
import com.mariejuana.expensetracker.application.AppViewModelProvider
import com.mariejuana.expensetracker.ui.expense.toFormattedDateTimeString
import com.mariejuana.expensetracker.ui.navigation.NavigationDestination
import java.text.NumberFormat

object BudgetDestination : NavigationDestination {
    override val route = "budget_screen"
    override val titleRes = R.string.budget_title
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun BudgetScreen(
    navigateToAddBudget: () -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: BudgetScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val currentBudget by viewModel.currentBudget.collectAsState()
    val allTransactionHistory by viewModel.transactionFragmentUiState.collectAsState()
    val transactionUiState by viewModel.transactionFragmentUiState.collectAsState()

    Scaffold (
        topBar = {
            ExpenseTopAppBar(
                title = stringResource(BudgetDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
            )
        }
    ) {  innerPadding ->
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.budget_available),
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    if (currentBudget?.amount == null) {
                        Text(
                            text = NumberFormat.getCurrencyInstance().format(0.0),
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.titleLarge
                        )
                    } else {
                        Text(
                            text = NumberFormat.getCurrencyInstance().format(currentBudget?.amount ?: 0.0),
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth(),
            ) {
                var deleteConfirmationRequiredTransaction by rememberSaveable { mutableStateOf(false) }

                val deleteButtonColors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )

                Column (
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        onClick = navigateToAddBudget,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Row (
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(id = R.string.budget_add_button),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    FilledTonalButton(
                        onClick = { deleteConfirmationRequiredTransaction = true },
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Row (
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteForever,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = stringResource(R.string.budget_history_delete_button),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                if (deleteConfirmationRequiredTransaction) {
                    DeleteConfirmationTransactionDialog(
                        onDeleteConfirm = {
                            deleteConfirmationRequiredTransaction = false
                            coroutineScope.launch {
                                viewModel.deleteAllTransaction()
                                navigateBack()
                            }
                        },
                        onDeleteCancel = { deleteConfirmationRequiredTransaction = false },
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                    )
                }
            }

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 25.dp, bottom = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.budget_history),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (transactionUiState.transactionList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.no_transaction_database),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            } else {
                LazyColumn() {
                    items(
                        items = allTransactionHistory.transactionList
                            .sortedByDescending { it.date },
                        key = { it.id }
                    ) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(id = R.dimen.padding_medium)),
                            ) {
                                Column {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = item.date.toFormattedDateTimeString(),
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                if (item.type == "expense") {
                                    Text(
                                        text = "- ${
                                            NumberFormat.getCurrencyInstance().format(item.amount)
                                        }",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                } else if (item.type == "budget") {
                                    Text(
                                        text = "+ ${
                                            NumberFormat.getCurrencyInstance().format(item.amount)
                                        }",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.align(Alignment.CenterVertically),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmationTransactionDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog (
        onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_all_transac_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(R.string.yes))
            }
        }
    )
}