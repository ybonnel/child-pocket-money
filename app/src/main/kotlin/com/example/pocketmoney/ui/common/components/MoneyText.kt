package com.example.pocketmoney.ui.common.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.pocketmoney.core.money.Money
import com.example.pocketmoney.core.money.MoneyFormatter

@Composable
fun MoneyText(
    money: Money,
    currencyCode: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    showSign: Boolean = false,
    positiveColor: Color = MaterialTheme.colorScheme.primary,
    negativeColor: Color = MaterialTheme.colorScheme.error,
    neutralColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    val text = if (showSign) {
        MoneyFormatter.formatSigned(money, currencyCode)
    } else {
        MoneyFormatter.format(money.absoluteValue, currencyCode)
    }
    val color = when {
        money.isPositive -> positiveColor
        money.isNegative -> negativeColor
        else -> neutralColor
    }
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier,
    )
}
