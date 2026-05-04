package com.example.pocketmoney.domain.model

enum class TransactionType {
    ALLOWANCE,  // Versement automatique hebdomadaire
    CREDIT,     // Crédit manuel
    DEBIT,      // Débit manuel
    ADJUSTMENT  // Ajustement
}
