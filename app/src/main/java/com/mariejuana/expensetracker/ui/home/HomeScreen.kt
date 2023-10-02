package com.mariejuana.expensetracker.ui.home

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import com.mariejuana.expensetracker.data.Expense
import com.mariejuana.expensetracker.data.ExpenseDao
import com.mariejuana.expensetracker.ui.theme.ExpenseTrackerTheme
import com.mariejuana.expensetracker.ui.navigation.NavigationDestination
import com.mariejuana.expensetracker.R
import com.mariejuana.expensetracker.application.AppViewModelProvider
import com.mariejuana.expensetracker.application.ExpenseTopAppBar

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

fun Date.toFormattedDateString(): String {
    val dateFormatter = java.text.SimpleDateFormat("MM/dd/yyyy", Locale.US)
    return dateFormatter.format(this)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navigateToExpenseEntry: () -> Unit,
    navigateToViewExpense: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val currentMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(Date())
    val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val totalPriceForCurrentMonth = viewModel.totalPriceForCurrentMonth.collectAsState().value

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
                ) {
                    Text(
                        text = "Current Budget P1212",
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
                onClick = navigateToExpenseEntry,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.expense_entry_button))
            }
            Button(
                onClick = navigateToViewExpense,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.expense_view))
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


@Preview(showBackground = true)
@Composable
fun RecentExpensePreview() {
    ExpenseTrackerTheme {
        RecentExpense(
            Expense(0,"Cookie","Food / Drink",100.0, Date()))
    }
}
