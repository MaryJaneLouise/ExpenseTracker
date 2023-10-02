package com.mariejuana.expensetracker.ui.expense.yearly

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.mariejuana.expensetracker.ui.expense.general_details.GeneralDetailsDestination
import com.mariejuana.expensetracker.ui.expense.general_details.GeneralDetailsScreenViewModel


object YearlyScreenDestination : NavigationDestination {
    override val route = "yearly_expenses"
    override val titleRes = R.string.expense_yearly_title
}

fun Date.toFormattedDateString(): String {
    val dateFormatter = java.text.SimpleDateFormat("MM/dd/yyyy", Locale.US)
    return dateFormatter.format(this)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun YearlyDetailsScreen (
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: YearlyScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val currentMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(Date())
    val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val totalPriceForCurrentYear = viewModel.totalPriceForCurrentYear.collectAsState().value

    val yearlyTotals by viewModel.totalPriceForYears.collectAsState()

    Scaffold (
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ExpenseTopAppBar(
                title = stringResource(YearlyScreenDestination.titleRes),
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Text(
                    text = "Expenses this year:",
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = NumberFormat.getCurrencyInstance().format(totalPriceForCurrentYear),
                    modifier = Modifier.padding(16.dp)
                )
            }
            YearlyExpenseCard(yearlyTotals)
        }
    }
}

@Composable
fun YearlyExpenseCard(yearlyTotals: List<Pair<String, Double?>>) {
    LazyColumn {
        items(yearlyTotals) { yearlyTotal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Year: ${yearlyTotal.first}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Total Expense: ${yearlyTotal.second ?: "N/A"}")
                }
            }
        }
    }
}
