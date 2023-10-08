package com.mariejuana.expensetracker.ui.expense.general_details

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import com.mariejuana.expensetracker.data.expense.Expense
import com.mariejuana.expensetracker.ui.navigation.NavigationDestination
import com.mariejuana.expensetracker.R
import com.mariejuana.expensetracker.application.AppViewModelProvider
import com.mariejuana.expensetracker.application.ExpenseTopAppBar

object GeneralDetailsDestination : NavigationDestination {
    override val route = "general_details"
    override val titleRes = R.string.expense_detail_title
}

fun Date.toFormattedDateString(): String {
    val dateFormatter = java.text.SimpleDateFormat("MM/dd/yyyy", Locale.US)
    return dateFormatter.format(this)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GeneralDetailsScreen (
    navigateToExpensePerMonth: () -> Unit,
    navigateToExpensePerYear: () -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: GeneralDetailsScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val currentMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(Date())
    val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val totalPriceForCurrentMonth = viewModel.totalPriceForCurrentMonth.collectAsState().value
    val totalPriceForCurrentYear = viewModel.totalPriceForCurrentYear.collectAsState().value

    val dummyExpense = Expense(
        id = 0,
        name = "",
        type = "",
        amount = 0.0,
        date_added = Date(0)
    )

    val recentExpense by viewModel.recentExpense.collectAsState(initial = dummyExpense)

    Scaffold (
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ExpenseTopAppBar(
                title = stringResource(GeneralDetailsDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column (
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
                ) {
                    Text(
                        text = "Expenses for $currentYear:",
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance().format(totalPriceForCurrentYear),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Expenses for $currentMonth $currentYear:",
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance().format(totalPriceForCurrentMonth),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            recentExpense?.let { RecentExpense(expense = it) }

            Button(
                onClick = navigateToExpensePerMonth,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.expense_view_month_button))
            }
            Button(
                onClick = navigateToExpensePerYear,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.expense_view_year_button))
            }
            Button(
                onClick = {},
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.expenses_delete_all))
            }
        }
    }
}

@Composable
private fun RecentExpense (
    expense: Expense,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = R.string.expense_recent_title),
        style = MaterialTheme.typography.titleLarge
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
                        text = expense.date_added.toFormattedDateString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = expense.type,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = NumberFormat.getCurrencyInstance().format(expense.amount),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}