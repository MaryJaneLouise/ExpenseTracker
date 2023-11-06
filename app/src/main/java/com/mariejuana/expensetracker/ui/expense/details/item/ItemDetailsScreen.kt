package com.mariejuana.expensetracker.ui.expense.details.item

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.mariejuana.expensetracker.ui.expense.entry.toExpense
import kotlinx.coroutines.launch
import java.text.DecimalFormat

object ItemDetailsDestination : NavigationDestination {
    override val route = "item_details"
    override val titleRes = R.string.expense_detail_title
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

fun Date.toFormattedDateString(): String {
    val dateFormatter = java.text.SimpleDateFormat("MM/dd/yyyy", Locale.US)
    return dateFormatter.format(this)
}

fun Date.toFormattedDateTimeString(): String {
    val dateFormatter = java.text.SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US)
    return dateFormatter.format(this)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExpenseDetailsScreen (
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: ItemDetailsScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState = viewModel.itemDetailsUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val currentMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(Date())
    val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold (
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ExpenseTopAppBar(
                title = stringResource(ItemDetailsDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        ExpenseDetailsBody(
            itemDetailsUiState = uiState.value,
            onDelete = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be deleted from the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.deleteItem()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun ExpenseDetailsBody(
    itemDetailsUiState: ItemDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemDetailsScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current

        val deleteButtonColors = ButtonDefaults.textButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )

        ItemDetails(
            expense = itemDetailsUiState.expenseDetails.toExpense(),
            modifier = Modifier.fillMaxWidth()
        )

        FilledTonalButton(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (viewModel.loadSettingsForceDelete(context)) {
                    deleteConfirmationRequired = false
                    onDelete()
                    Toast.makeText(context, "Expense deleted successfully.", Toast.LENGTH_LONG).show()
                } else {
                    deleteConfirmationRequired = true
                }
            },
            colors = deleteButtonColors
        ) {
            Row (
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.DeleteForever,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = stringResource(R.string.delete),
                    style = MaterialTheme.typography.bodyLarge)
            }
        }

        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(onDeleteConfirm = {
                deleteConfirmationRequired = false
                onDelete()
                Toast.makeText(context, "Expense deleted successfully.", Toast.LENGTH_LONG).show()
            },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Composable
fun ItemDetails(
    expense: Expense,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = expense.name,
        onValueChange = {},
        label = { Text(stringResource(R.string.expense_entry_name)) },
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = MaterialTheme.colorScheme.primary,
            disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.fillMaxWidth(),
        enabled = false
    )
    OutlinedTextField(
        value = expense.type,
        onValueChange = {},
        label = { Text(stringResource(R.string.expense_detail_type)) },
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = MaterialTheme.colorScheme.primary,
            disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.fillMaxWidth(),
        enabled = false
    )
    OutlinedTextField(
        value = NumberFormat.getCurrencyInstance().format(expense.amount),
        onValueChange = {},
        label = { Text(stringResource(R.string.expense_detail_amount)) },
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = MaterialTheme.colorScheme.primary,
            disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.fillMaxWidth(),
        enabled = false
    )
    OutlinedTextField(
        value = expense.date_added.toFormattedDateTimeString(),
        onValueChange = {},
        label = { Text(stringResource(R.string.expense_detail_date_added)) },
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = MaterialTheme.colorScheme.primary,
            disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.fillMaxWidth(),
        enabled = false
    )
}


@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
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