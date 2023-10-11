package com.mariejuana.expensetracker.ui.budget

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
                        .padding(8.dp),
                ) {
                    Text(
                        text = "Current Budget:\n",
                        modifier = Modifier.padding(16.dp)
                    )
                    if (currentBudget?.amount == null) {
                        Text(
                            text = NumberFormat.getCurrencyInstance().format(0.0),
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        Text(
                            text = NumberFormat.getCurrencyInstance().format(currentBudget?.amount ?: 0.0),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = navigateToAddBudget
                ) {
                    Text(
                        text = stringResource(id = R.string.budget_add_button)
                    )
                }
            }

            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(text = "Transaction History")
            }

            LazyColumn() {
                items(
                    items = allTransactionHistory.transactionList
                        .sortedByDescending { it.date },
                    key = { it.id }
                ) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(id = R.dimen.padding_medium)),
                        ) {
                            Column {
                                Text(
                                    text = item.name,
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
                                )
                            } else if (item.type == "budget") {
                                Text(
                                    text = "+ ${
                                        NumberFormat.getCurrencyInstance().format(item.amount)
                                    }",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}