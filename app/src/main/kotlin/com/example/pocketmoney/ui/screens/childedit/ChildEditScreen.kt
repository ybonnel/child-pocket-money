package com.example.pocketmoney.ui.screens.childedit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pocketmoney.R
import com.example.pocketmoney.ui.theme.AvatarColors
import kotlinx.datetime.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChildEditScreen(
    childId: Long?,
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: ChildEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(childId) {
        viewModel.loadChild(childId)
    }

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) onSaved()
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.action_delete)) },
            text = { Text(stringResource(R.string.child_edit_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete()
                }) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (childId == null) stringResource(R.string.child_edit_title_new)
                        else stringResource(R.string.child_edit_title_edit)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                },
                actions = {
                    if (childId != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name field
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.child_edit_name)) },
                placeholder = { Text(stringResource(R.string.child_edit_name_hint)) },
                isError = uiState.nameError,
                supportingText = if (uiState.nameError) {
                    { Text(stringResource(R.string.child_edit_name_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            // Color picker
            Text(
                text = stringResource(R.string.child_edit_color),
                style = MaterialTheme.typography.labelLarge,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AvatarColors.forEach { color ->
                    val argb = color.toArgb()
                    val isSelected = uiState.colorArgb == argb
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (isSelected) Modifier.border(
                                    3.dp,
                                    MaterialTheme.colorScheme.onSurface,
                                    CircleShape
                                )
                                else Modifier
                            )
                            .clickable { viewModel.onColorChange(argb) }
                    )
                }
            }

            // Weekly allowance
            OutlinedTextField(
                value = uiState.weeklyAllowanceStr,
                onValueChange = viewModel::onAllowanceChange,
                label = { Text(stringResource(R.string.child_edit_allowance)) },
                placeholder = { Text(stringResource(R.string.child_edit_allowance_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            // Allowance day of week
            DayOfWeekDropdown(
                selected = uiState.allowanceDayOfWeek,
                onSelected = viewModel::onAllowanceDayChange,
            )

            // Allowance active toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.child_edit_allowance_active),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Switch(
                    checked = uiState.allowanceActive,
                    onCheckedChange = viewModel::onAllowanceActiveChange,
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(R.string.child_edit_save))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayOfWeekDropdown(
    selected: DayOfWeek,
    onSelected: (DayOfWeek) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val days = DayOfWeek.entries.toList()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = dayOfWeekLabel(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.child_edit_allowance_day)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            days.forEach { day ->
                DropdownMenuItem(
                    text = { Text(dayOfWeekLabel(day)) },
                    onClick = {
                        onSelected(day)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun dayOfWeekLabel(day: DayOfWeek): String = when (day) {
    DayOfWeek.MONDAY -> stringResource(R.string.day_monday)
    DayOfWeek.TUESDAY -> stringResource(R.string.day_tuesday)
    DayOfWeek.WEDNESDAY -> stringResource(R.string.day_wednesday)
    DayOfWeek.THURSDAY -> stringResource(R.string.day_thursday)
    DayOfWeek.FRIDAY -> stringResource(R.string.day_friday)
    DayOfWeek.SATURDAY -> stringResource(R.string.day_saturday)
    DayOfWeek.SUNDAY -> stringResource(R.string.day_sunday)
    else -> day.name
}
