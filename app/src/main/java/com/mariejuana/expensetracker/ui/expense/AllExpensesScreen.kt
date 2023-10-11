package com.mariejuana.expensetracker.ui.expense

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import com.mariejuana.expensetracker.ui.navigation.NavigationDestination
import com.mariejuana.expensetracker.R
import com.mariejuana.expensetracker.application.AppViewModelProvider
import com.mariejuana.expensetracker.application.ExpenseTopAppBar
import java.util.Calendar

object AllExpensesScreenDestination : NavigationDestination {
    override val route = "all_expenses"
    override val titleRes = R.string.all_expenses_title
}

fun Date.toFormattedDateString(): String {
    val dateFormatter = java.text.SimpleDateFormat("MM/dd/yyyy", Locale.US)
    return dateFormatter.format(this)
}

fun Date.toFormattedDateTimeString(): String {
    val dateFormatter = java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US)
    return dateFormatter.format(this)
}

fun Date.toFormattedMonthString(): String {
    val dateFormatter = java.text.SimpleDateFormat("MM", Locale.US)
    return dateFormatter.format(this)
}

fun Int.toFormattedFullMonthString(): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, this.toString().toInt() - 1) // Months are 0-indexed in Calendar
    val date = calendar.time

    val dateFormatter = java.text.SimpleDateFormat("MMMM", Locale.US)
    return dateFormatter.format(date)
}

fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun AllExpensesScreen (
    onNavigateUp: () -> Unit,
    navigateBack: () -> Unit,
    navigateToExpenseDetails: (Int) -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: AllExpensesScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val perMonthExpense by viewModel.perMonthScreenUiState.collectAsState()
    val currentMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(Date())
    val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

    val textState = remember { mutableStateOf(TextFieldValue("")) }

    val searchedText = textState.value.text

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold (
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ExpenseTopAppBar(
                title = stringResource(AllExpensesScreenDestination.titleRes),
                canNavigateBack = canNavigateBack,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            Column (
            ) {
                SearchView(state = textState, placeHolder = "Search items")

                LazyColumn() {
                    items(
                        items = perMonthExpense.expenseList
                            .sortedByDescending { it.date_added }
                            .filter {
                                it.name.contains(searchedText, ignoreCase = true)
                            },
                        key = { it.id }
                    ) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            onClick = { navigateToExpenseDetails(item.id) },
                        ) {
                            Row (
                                modifier = Modifier.fillMaxWidth().padding(dimensionResource(id = R.dimen.padding_medium)),
                            ) {
                                Column {
                                    Text(
                                        text = item.name,
                                    )
                                    Text(
                                        text = item.date_added.toFormattedDateTimeString(),
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = NumberFormat.getCurrencyInstance().format(item.amount),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    state: MutableState<TextFieldValue>,
    placeHolder: String
) {
    OutlinedTextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        label = { Text(text = placeHolder) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_large))
    )
}