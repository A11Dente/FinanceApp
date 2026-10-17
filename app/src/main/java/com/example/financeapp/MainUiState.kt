package com.example.financeapp

internal data class MainUiState(
    val balance: Int = 0,
    val count: Int = 0,
    // Какое начальное значение у баланса?
    // Как посчитать выполненные операции?
)

internal enum class TransactionType {
    DEPOSIT,
    WITHDRAW,
    // Какие два типа нужны кнопкам?
}

internal data class Transaction(
    val amount: Int = 0,
    val type: TransactionType,
    // Какие два свойства полностью описывают операцию?
)

internal sealed interface MainUiEvent {
    data class AddTransaction(val transaction: Transaction) : MainUiEvent
    // Одно событие должно переносить созданную транзакцию
}

internal fun reduce(state: MainUiState, event: MainUiEvent): MainUiState = when (event) {
    is MainUiEvent.AddTransaction -> {
        val balancenew = when (event.transaction.type) {
            TransactionType.DEPOSIT -> state.balance + event.transaction.amount
            TransactionType.WITHDRAW -> state.balance - event.transaction.amount
        }
        state.copy(count = state.count + 1, balance = balancenew)
    }
    // Получите данные события и верните новое состояние
}
