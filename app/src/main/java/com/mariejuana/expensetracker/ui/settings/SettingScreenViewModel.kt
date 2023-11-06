package com.mariejuana.expensetracker.ui.settings

import android.app.Application
import android.content.Context
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SettingScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val _isForceDialogInsertOffEnabled = MutableLiveData<Boolean>()
    private val _isForceDialogDeleteOffEnabled = MutableLiveData<Boolean>()
    private val _isForceDarkModeEnabled = MutableLiveData<Boolean>()
    private val _isForceNoBudgetRequiredEnabled = MutableLiveData<Boolean>()

    val forceDialogInsertOffEnabled: LiveData<Boolean> get() = _isForceDialogInsertOffEnabled
    val forceDialogDeleteOffEnabled: LiveData<Boolean> get() = _isForceDialogDeleteOffEnabled
    val forceDarkModeEnabled: LiveData<Boolean> get() = _isForceDarkModeEnabled
    val forceNoBudgetRequiredEnabled: LiveData<Boolean> get() = _isForceNoBudgetRequiredEnabled

    init {
        loadSettingsNoBudgetRequired(getApplication())
        loadSettingsForceInsert(getApplication())
        loadSettingsForceDelete(getApplication())
        loadSettingsForceDarkMode(getApplication())
    }
    fun loadSettingsForceInsert(context: Context) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        _isForceDialogInsertOffEnabled.value = sharedPreferences.getBoolean("forceDialogInsertOff", false)
    }

    fun loadSettingsForceDelete(context: Context) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        _isForceDialogDeleteOffEnabled.value = sharedPreferences.getBoolean("forceDialogDeleteOff", false)
    }

    fun loadSettingsForceDarkMode(context: Context) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        _isForceDarkModeEnabled.value = sharedPreferences.getBoolean("forceDarkMode", false)
    }

    fun loadSettingsNoBudgetRequired(context: Context) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        _isForceNoBudgetRequiredEnabled.value = sharedPreferences.getBoolean("forceNoBudget", false)
    }

    fun saveSettingsForceInsert(context: Context, isEnabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            putBoolean("forceDialogInsertOff", isEnabled)
            apply()
        }
        _isForceDialogInsertOffEnabled.value = isEnabled
    }

    fun saveSettingsForceDelete(context: Context, isEnabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            putBoolean("forceDialogDeleteOff", isEnabled)
            apply()
        }
        _isForceDialogDeleteOffEnabled.value = isEnabled
    }

    fun saveSettingsForceDarkMode(context: Context, isEnabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            putBoolean("forceDarkMode", isEnabled)
            apply()
        }
        _isForceDarkModeEnabled.value = isEnabled
    }

    fun saveSettingsForceNoBudgetRequired(context: Context, isEnabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            putBoolean("forceNoBudget", isEnabled)
            apply()
        }
        _isForceNoBudgetRequiredEnabled.value = isEnabled
    }
}