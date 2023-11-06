package com.mariejuana.expensetracker.ui.settings

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Repartition
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
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
import com.mariejuana.expensetracker.ui.home.HomeDestination
import kotlinx.coroutines.launch


object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.setting_title
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun SettingScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: SettingScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val isForceDialogOffInsertEnabled = remember { mutableStateOf(false) }
    val isForceDialogOffDeleteEnabled = remember { mutableStateOf(false) }
    val isForceDarkModeEnabled = remember { mutableStateOf(false) }
    val isForceNoBudgetRequiredEnabled = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = viewModel) {
        viewModel.forceDialogInsertOffEnabled.observeForever { value ->
            isForceDialogOffInsertEnabled.value = value
        }
        viewModel.forceDialogDeleteOffEnabled.observeForever { value ->
            isForceDialogOffDeleteEnabled.value = value
        }
        viewModel.forceDarkModeEnabled.observeForever { value ->
            isForceDarkModeEnabled.value = value
        }
        viewModel.forceNoBudgetRequiredEnabled.observeForever { value ->
            isForceNoBudgetRequiredEnabled.value = value
        }
    }

    val context = LocalContext.current

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ExpenseTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                canNavigateBack = canNavigateBack,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack
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
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.setting_enable_force_dialog_insert_off),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Switch(
                                checked = isForceDialogOffInsertEnabled.value,
                                onCheckedChange = { isChecked ->
                                    viewModel.saveSettingsForceInsert(context, isChecked)
                                },
                                modifier = Modifier.align(Alignment.CenterVertically),
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.setting_enable_force_dialog_delete_off),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Switch(
                                checked = isForceDialogOffDeleteEnabled.value,
                                onCheckedChange = { isChecked ->
                                    viewModel.saveSettingsForceDelete(context, isChecked)
                                },
                                modifier = Modifier.align(Alignment.CenterVertically),
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(1.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.setting_enable_force_no_budget_required),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Switch(
                                checked = isForceNoBudgetRequiredEnabled.value,
                                onCheckedChange = { isChecked ->
                                    viewModel.saveSettingsForceNoBudgetRequired(context, isChecked)
                                },
                                enabled = true
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(1.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.setting_enable_force_dark_mode),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Switch(
                                checked = isForceDarkModeEnabled.value,
                                onCheckedChange = { isChecked ->
                                    viewModel.saveSettingsForceDarkMode(context, isChecked)
                                },
                                enabled = false
                            )
                        }
                    }
                }
            }
        }
    }
}