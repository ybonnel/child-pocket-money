package com.ybonnel.childpocketmoney.ui.screens.childdetail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ybonnel.childpocketmoney.R
import com.ybonnel.childpocketmoney.core.money.Money
import com.ybonnel.childpocketmoney.domain.model.Transaction
import com.ybonnel.childpocketmoney.domain.model.TransactionType
import com.ybonnel.childpocketmoney.ui.common.components.ChildAvatar
import com.ybonnel.childpocketmoney.ui.common.components.EmptyState
import com.ybonnel.childpocketmoney.ui.common.components.MoneyText
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildDetailScreen(
    childId: Long,
    onNavigateBack: () -> Unit,
    onEditChild: (Long) -> Unit,
    onAddTransaction: (Long, Int) -> Unit,
    viewModel: ChildDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val deleteMsg = stringResource(R.string.child_detail_transaction_deleted)
    val undoLabel = stringResource(R.string.child_detail_undo)

    LaunchedEffect(childId) {
        viewModel.loadChild(childId)
    }

    // Handle snackbar for deleted transaction.
    // pendingDelete changes each time a new item is swiped — LaunchedEffect restarts,
    // cancelling the previous snackbar so the new one is shown immediately.
    LaunchedEffect(uiState.pendingDelete) {
        if (uiState.pendingDelete != null) {
            val result = snackbarHostState.showSnackbar(
                message = deleteMsg,
                actionLabel = undoLabel,
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.clearPendingDelete()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.child?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEditChild(childId) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.child_detail_edit)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = { onAddTransaction(childId, -1) },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.padding(bottom = 8.dp),
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                LargeFloatingActionButton(
                    onClick = { onAddTransaction(childId, +1) },
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.child_detail_add_credit)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 160.dp),
        ) {
            // Balance header
            item {
                BalanceHeader(
                    childName = uiState.child?.name ?: "",
                    colorArgb = uiState.child?.colorArgb ?: 0xFF4CAF50.toInt(),
                    balance = uiState.balance,
                    currencyCode = uiState.currencyCode,
                )
            }

            item {
                Text(
                    text = stringResource(R.string.child_detail_history),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            if (uiState.transactions.isEmpty()) {
                item {
                    EmptyState(
                        message = stringResource(R.string.child_detail_history_empty),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                    )
                }
            } else {
                items(
                    items = uiState.transactions,
                    key = { it.id }
                ) { transaction ->
                    SwipeToDeleteTransaction(
                        transaction = transaction,
                        currencyCode = uiState.currencyCode,
                        onDelete = { viewModel.deleteTransaction(transaction) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceHeader(
    childName: String,
    colorArgb: Int,
    balance: Money,
    currencyCode: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ChildAvatar(name = childName, colorArgb = colorArgb, size = 72.dp)
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.child_detail_balance),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(4.dp))
            MoneyText(
                money = balance,
                currencyCode = currencyCode,
                style = MaterialTheme.typography.displaySmall,
                positiveColor = MaterialTheme.colorScheme.primary,
                negativeColor = MaterialTheme.colorScheme.error,
                neutralColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteTransaction(
    transaction: Transaction,
    currencyCode: String,
    onDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        },
        positionalThreshold = { it * 0.4f }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                    else -> Color.Transparent
                },
                label = "swipe_bg_color"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) {
        TransactionItem(transaction = transaction, currencyCode = currencyCode)
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    currencyCode: String,
) {
    val tz = TimeZone.currentSystemDefault()
    val date = transaction.occurredAt.toLocalDateTime(tz).date
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (transaction.label.isBlank()) {
                        transactionTypeLabel(transaction.type)
                    } else transaction.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "${date.dayOfMonth}/${date.monthNumber}/${date.year}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (transaction.type == TransactionType.ALLOWANCE) {
                    Text(
                        text = "🔄 Auto",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            MoneyText(
                money = transaction.amount,
                currencyCode = currencyCode,
                showSign = true,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun transactionTypeLabel(type: TransactionType): String = when (type) {
    TransactionType.ALLOWANCE -> stringResource(R.string.transaction_type_allowance)
    TransactionType.CREDIT -> stringResource(R.string.transaction_type_credit)
    TransactionType.DEBIT -> stringResource(R.string.transaction_type_debit)
    TransactionType.ADJUSTMENT -> stringResource(R.string.transaction_type_adjustment)
}
