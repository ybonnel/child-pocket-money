package com.ybonnel.childpocketmoney.ui.screens.childedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ybonnel.childpocketmoney.core.money.Money
import com.ybonnel.childpocketmoney.domain.model.Child
import com.ybonnel.childpocketmoney.domain.usecase.child.AddChildUseCase
import com.ybonnel.childpocketmoney.domain.usecase.child.DeleteChildUseCase
import com.ybonnel.childpocketmoney.domain.usecase.child.ObserveChildUseCase
import com.ybonnel.childpocketmoney.domain.usecase.child.UpdateChildUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class ChildEditViewModel @Inject constructor(
    private val observeChild: ObserveChildUseCase,
    private val addChild: AddChildUseCase,
    private val updateChild: UpdateChildUseCase,
    private val deleteChild: DeleteChildUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChildEditUiState())
    val uiState: StateFlow<ChildEditUiState> = _uiState.asStateFlow()

    fun loadChild(childId: Long?) {
        if (childId == null) return
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            // Use .first() instead of .collect() to avoid resetting the form if the DB
            // emits again while the user is editing fields.
            val child = observeChild(childId).first() ?: return@launch
            val centsStr = "${child.weeklyAllowance.cents / 100}.${
                (child.weeklyAllowance.cents % 100).toString().padStart(2, '0')
            }"
            _uiState.update {
                it.copy(
                    childId = child.id,
                    name = child.name,
                    colorArgb = child.colorArgb,
                    weeklyAllowanceStr = centsStr,
                    allowanceDayOfWeek = child.allowanceDayOfWeek,
                    allowanceActive = child.allowanceActive,
                    isLoading = false,
                )
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = false) }
    }

    fun onColorChange(colorArgb: Int) {
        _uiState.update { it.copy(colorArgb = colorArgb) }
    }

    fun onAllowanceChange(str: String) {
        _uiState.update { it.copy(weeklyAllowanceStr = str) }
    }

    fun onAllowanceDayChange(day: DayOfWeek) {
        _uiState.update { it.copy(allowanceDayOfWeek = day) }
    }

    fun onAllowanceActiveChange(active: Boolean) {
        _uiState.update { it.copy(allowanceActive = active) }
    }

    fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = true) }
            return
        }
        val allowanceCents = Money.fromString(state.weeklyAllowanceStr)?.cents ?: 0L
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                val child = Child(
                    id = state.childId ?: 0L,
                    name = state.name.trim(),
                    colorArgb = state.colorArgb,
                    weeklyAllowance = Money(allowanceCents),
                    allowanceDayOfWeek = state.allowanceDayOfWeek,
                    allowanceActive = state.allowanceActive,
                )
                if (state.childId == null) {
                    addChild(child)
                } else {
                    updateChild(child)
                }
                _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, error = "Erreur lors de l'enregistrement")
                }
            }
        }
    }

    fun delete() {
        val childId = _uiState.value.childId ?: return
        viewModelScope.launch {
            try {
                deleteChild(childId)
                _uiState.update { it.copy(savedSuccessfully = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erreur lors de la suppression") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
