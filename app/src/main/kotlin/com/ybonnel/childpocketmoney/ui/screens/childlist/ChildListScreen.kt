package com.ybonnel.childpocketmoney.ui.screens.childlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ybonnel.childpocketmoney.R
import com.ybonnel.childpocketmoney.core.money.Money
import com.ybonnel.childpocketmoney.core.money.MoneyFormatter
import com.ybonnel.childpocketmoney.ui.common.components.ChildAvatar
import com.ybonnel.childpocketmoney.ui.common.components.EmptyState
import com.ybonnel.childpocketmoney.ui.common.components.MoneyText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildListScreen(
    onChildClick: (Long) -> Unit,
    onAddChild: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: ChildListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.child_list_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.nav_settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddChild) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.child_list_add)
                )
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.children.isEmpty() -> {
                EmptyState(
                    message = stringResource(R.string.child_list_empty),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        items = uiState.children,
                        key = { it.child.id }
                    ) { childWithBalance ->
                        ChildCard(
                            childWithBalance = childWithBalance,
                            currencyCode = uiState.currencyCode,
                            onClick = { onChildClick(childWithBalance.child.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChildCard(
    childWithBalance: ChildWithBalance,
    currencyCode: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChildAvatar(
                name = childWithBalance.child.name,
                colorArgb = childWithBalance.child.colorArgb,
                size = 56.dp,
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = childWithBalance.child.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                val balance = Money(childWithBalance.balanceCents)
                val allowance = childWithBalance.child.weeklyAllowance
                if (allowance.cents > 0) {
                    Text(
                        text = stringResource(
                            R.string.child_allowance,
                            MoneyFormatter.format(allowance, currencyCode)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            MoneyText(
                money = Money(childWithBalance.balanceCents),
                currencyCode = currencyCode,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
