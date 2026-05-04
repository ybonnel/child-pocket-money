package com.example.pocketmoney.ui.screens.transactionedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pocketmoney.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditScreen(
    childId: Long,
    initialSign: Int,
    onDismiss: () -> Unit,
    onSaved: () -> Unit,
    viewModel: TransactionEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(initialSign) {
        viewModel.setInitialSign(initialSign)
    }

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isCredit) stringResource(R.string.transaction_edit_title_credit)
                        else stringResource(R.string.transaction_edit_title_debit)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Credit / Debit toggle
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = uiState.isCredit,
                    onClick = { viewModel.onSignToggle(true) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                ) {
                    Text("+ ${stringResource(R.string.transaction_edit_title_credit)}")
                }
                SegmentedButton(
                    selected = !uiState.isCredit,
                    onClick = { viewModel.onSignToggle(false) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.errorContainer,
                    )
                ) {
                    Text("- ${stringResource(R.string.transaction_edit_title_debit)}")
                }
            }

            // Amount field
            OutlinedTextField(
                value = uiState.amountStr,
                onValueChange = viewModel::onAmountChange,
                label = { Text(stringResource(R.string.transaction_edit_amount)) },
                isError = uiState.amountError,
                supportingText = if (uiState.amountError) {
                    { Text(stringResource(R.string.transaction_edit_amount_error)) }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("0.00") },
            )

            // Label field
            OutlinedTextField(
                value = uiState.label,
                onValueChange = viewModel::onLabelChange,
                label = { Text(stringResource(R.string.transaction_edit_label)) },
                placeholder = { Text(stringResource(R.string.transaction_edit_label_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.save(childId) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
                colors = if (!uiState.isCredit) ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ) else ButtonDefaults.buttonColors()
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(R.string.transaction_edit_confirm))
                }
            }
        }
    }
}
