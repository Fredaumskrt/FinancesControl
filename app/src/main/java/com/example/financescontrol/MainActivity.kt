package com.example.financescontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FinanceAppTheme {
                FinanceControlScreen()
            }
        }
    }
}

// 1. Modelo de dados simplificado
data class Transaction(
    val id: Int,
    val description: String,
    val amount: Double,
    val isIncome: Boolean,
    val date: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
)

val euroFormat = NumberFormat.getCurrencyInstance().apply {
    currency = Currency.getInstance("EUR")
} // Reais -> Euro

// 2. Tela principal tudo junto
@Composable
fun FinanceControlScreen() {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }

    val balance = transactions.sumOf { if (it.isIncome) it.amount else -it.amount }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabeçalho
        Text(
            text = "Controle Financeiro",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Saldo
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (balance >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Saldo Atual",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = euroFormat.format(balance),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (balance >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }

            // Se for usar saldo BRL descomentar esta linha
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("Saldo Atual")
//                Text(
//                    NumberFormat.getCurrencyInstance().format(balance),
//                    style = MaterialTheme.typography.headlineMedium,
//                    color = if (balance >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
//                )
//            }
        }

        // Formulário
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = euroFormat.format(amount.toDoubleOrNull() ?: 0.0),
            onValueChange = { /* lógica de conversão */ },
            label = { Text("Valor") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RadioButton(
                selected = !isIncome,
                onClick = { isIncome = false }
            )
            Text("Despesa")

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = isIncome,
                onClick = { isIncome = true }
            )
            Text("Receita")
        }

        Button(
            onClick = {
                if (description.isNotBlank() && amount.isNotBlank()) {
                    transactions = transactions + Transaction(
                        id = transactions.size + 1,
                        description = description,
                        amount = amount.toDouble(),
                        isIncome = isIncome
                    )
                    description = ""
                    amount = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar")
        }

        // Histórico
        Text(
            "Últimas transações",
            style = MaterialTheme.typography.titleMedium
        )

        if (transactions.isEmpty()) {
            Text("Nenhuma transação ainda")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions.reversed()) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}

// 3. Componente de item da transação
@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(transaction.description)
                Text(
                    transaction.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Text(
                "${if (transaction.isIncome) "+" else "-"} ${NumberFormat.getCurrencyInstance().format(transaction.amount)}",
                color = if (transaction.isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}

// 4. Tema simplificado
@Composable
fun FinanceAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewFinanceApp() {
    FinanceAppTheme {
        FinanceControlScreen()
    }
}