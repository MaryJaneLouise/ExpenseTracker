package com.mariejuana.expensetracker.ui.expense.yearly

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.mariejuana.expensetracker.ui.navigation.NavigationDestination
import com.mariejuana.expensetracker.R
import com.mariejuana.expensetracker.application.AppViewModelProvider
import com.mariejuana.expensetracker.application.ExpenseTopAppBar


object YearlyScreenDestination : NavigationDestination {
    override val route = "yearly_expenses"
    override val titleRes = R.string.expense_yearly_title
}

fun Date.toFormattedDateString(): String {
    val dateFormatter = java.text.SimpleDateFormat("MM/dd/yyyy", Locale.US)
    return dateFormatter.format(this)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun YearlyDetailsScreen (
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: YearlyScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val currentMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(Date())
    val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()).toInt()

    val startYear = 2014
    val endYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()).toInt()
    val totalYears = endYear - startYear
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val totalPriceForCurrentYear = viewModel.totalPriceForCurrentYear.collectAsState().value


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
                    .padding(16.dp),
            ) {
                Text(
                    text = "Expenses this year:",
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = NumberFormat.getCurrencyInstance().format(totalPriceForCurrentYear),
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Column (
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                for (year in totalYears downTo 1) {
                    val actualYear = currentYear + year - totalYears
                    val totalPricePerYear = viewModel.yearlyExpenses[year - 1]
                    val totalPrice = totalPricePerYear.value
                    val formattedTotalPrice =
                        if (totalPrice != null) {
                            NumberFormat.getCurrencyInstance().format(totalPrice)
                        } else {
                            "N/A"
                        }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                    ) {
                        Row (
                            modifier = Modifier.fillMaxWidth().padding(dimensionResource(id = R.dimen.padding_medium)),
                        ) {
                            Column {
                                Text(
                                    text = actualYear.toString(),
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = formattedTotalPrice,
                            )
                        }
                    }
                }
            }
        }
    }
}