package com.ybonnel.childpocketmoney.domain.model

enum class TransactionType {
    ALLOWANCE,  // Versement automatique hebdomadaire
    CREDIT,     // Crédit manuel
    DEBIT,      // Débit manuel
    ADJUSTMENT  // Ajustement
}
