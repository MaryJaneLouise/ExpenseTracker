package com.mariejuana.expensetracker.ui.home

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import com.mariejuana.expensetracker.data.expense.Expense
import com.mariejuana.expensetracker.ui.theme.ExpenseTrackerTheme
import com.mariejuana.expensetracker.ui.navigation.NavigationDestination
import com.mariejuana.expensetracker.R
import com.mariejuana.expensetracker.application.AppViewModelProvider
import com.mariejuana.expensetracker.application.ExpenseTopAppBar
import com.mariejuana.expensetracker.data.expense.Budget
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

fun Date.toFormattedDateString(): String {
    val dateFormatter = java.text.SimpleDateFormat("MM/dd/yyyy", Locale.US)
    return dateFormatter.format(this)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    navigateToExpenseEntry: () -> Unit,
    navigateToViewExpense: () -> Unit,
    navigateToCurrentBudget: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    val currentMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(Date())
    val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val totalPriceForCurrentMonth = viewModel.totalPriceForCurrentMonth.collectAsState().value

    val dummyBudget = Budget(
        id = 0,
        amount = 0.0,
    )

    val dummyExpense = Expense(
        id = 0,
        name = "",
        type = "",
        amount = 0.0,
        date_added = Date(0)
    )

    val recentExpense by viewModel.recentExpense.collectAsState(initial = dummyExpense)

    val currentBudget by viewModel.currentBudget.collectAsState()

    Scaffold (
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ExpenseTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    onClick = navigateToCurrentBudget
                ) {
                    Text(
                        text = "Current Budget:",
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance().format(currentBudget?.amount ?: 0.0),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "$currentMonth $currentYear expenses:",
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance().format(totalPriceForCurrentMonth),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            recentExpense?.let { RecentExpense(expense = it) }

            var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

            val deleteButtonColors = ButtonDefaults.textButtonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )

            Button(
                onClick = navigateToExpenseEntry,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.expense_entry_button),
                    style = MaterialTheme.typography.bodyLarge)
            }
            Button(
                onClick = navigateToViewExpense,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.expense_view),
                    style = MaterialTheme.typography.bodyLarge)
            }
            Button(
                onClick = { deleteConfirmationRequired = true },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                colors = deleteButtonColors
            ) {
                Text(text = stringResource(R.string.delete_all_button),
                    style = MaterialTheme.typography.bodyLarge)
            }

            if (deleteConfirmationRequired) {
                DeleteConfirmationDialog(
                    onDeleteConfirm = {
                        deleteConfirmationRequired = false
                        coroutineScope.launch {
                            viewModel.deleteAllItem()
                            navigateBack()
                        }
                    },
                    onDeleteCancel = { deleteConfirmationRequired = false },
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecentExpense (
    expense: Expense,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.expense_recent_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card (
            modifier = modifier,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column (
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = expense.name,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = expense.type,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = NumberFormat.getCurrencyInstance().format(expense.amount),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_all_question)) },
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
        })
}

@Preview(showBackground = true)
@Composable
fun RecentExpensePreview() {
    ExpenseTrackerTheme {
        RecentExpense(
            Expense(0,"Cookie","Food / Drink",100.0, Date())
        )
    }
}
