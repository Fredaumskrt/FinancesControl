package com.example.financecontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FinanceControlTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    FinanceControlLayout()
                }
            }
        }
    }
}

// Modelo de dados para transações
data class Transaction(
    val id: Int,
    val description: String,
    val amount: Double,
    val type: String, // "income" ou "expense"
    val date: String
)

@Composable
fun FinanceControlLayout() {
    // Estados do aplicativo
    val description = remember { mutableStateOf("") }
    val amount = remember { mutableStateOf("") }
    val transactionType = remember { mutableStateOf("expense") }
    val transactions = remember { mutableStateOf(listOf<Transaction>()) }

    // teste para saldo atual

    val balanceTest = transactions.value.sumOf{
        if (it.type == "income") it.amount else - it.amount
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Controle Financeiro",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Exibe o saldo atual
        Text(
            text = "Saldo: ${NumberFormat.getCurrencyInstance().format(balanceTest)}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo de descrição
        TextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Descrição") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Campo de valor
        TextField(
            value = amount.value,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    amount.value = newValue
                }
            },
            label = { Text("Valor") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Seleção do tipo de transação
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            RadioButton(
                selected = transactionType.value == "expense",
                onClick = { transactionType.value = "expense" }
            )
            Text("Despesa", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = transactionType.value == "income",
                onClick = { transactionType.value = "income" }
            )
            Text("Receita")
        }

        //  adicionar transação
        Button(
            onClick = {
                if (description.value.isNotBlank() && amount.value.isNotBlank()) {
                    val newTransaction = Transaction(
                        id = transactions.value.size + 1,
                        description = description.value,
                        amount = amount.value.toDouble(),
                        type = transactionType.value,
                        date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    )
                    transactions.value = transactions.value + newTransaction
                    description.value = ""
                    amount.value = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Adicionar Transação")
        }

        // Lista de transações
        Text(
            text = "Histórico de Transações",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        if (transactions.value.isEmpty()) {
            Text(
                text = "Nenhuma transação registrada",
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(transactions.value.reversed()) { transaction ->
                    TransactionItem(transaction = transaction)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val amountColor = if (transaction.type == "income") {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${if (transaction.type == "income") "+" else "-"} ${NumberFormat.getCurrencyInstance().format(transaction.amount)}",
                color = amountColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = transaction.date,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun FinanceControlTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun FinanceControlPreview() {
    FinanceControlTheme {
        FinanceControlLayout()
    }
}