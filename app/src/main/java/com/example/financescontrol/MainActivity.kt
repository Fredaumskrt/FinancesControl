package com.example.financescontrol

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.financescontrol.ui.theme.FinanceControlScreen

import java.util.*

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FinanceControlTheme {
                FinanceControlLayout() // objetivo eh trazer a tela pra ca
            }
        }
    }
}
// Modelo de dados para transações
enum class TransactionCategory {
    ALIMENTACAO, TRANSPORTE, MORADIA, LAZER, SAUDE, EDUCACAO, SALARIO, OUTROS
}

data class Transaction(
    val id: Int,
    val description: String,
    val amount: Double,
    val type: String,
    val date: String,
    val category: TransactionCategory

)

@RequiresApi(Build.VERSION_CODES.O)
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
                try {
                    if (description.value.isNotBlank() && amount.value.isNotBlank()) {
                        val amountValue = amount.value.toDoubleOrNull()
                            ?: return@Button // Sai se não for número válido

                        transactions.value = transactions.value + Transaction(
                            id = System.currentTimeMillis().toInt(),
                            description = description.value,
                            amount = amountValue,
                            type = transactionType.value,
                            date = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            category = TransactionCategory.OUTROS
                        )
                        description.value = ""
                        amount.value = ""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun FinanceControlPreview() {
    FinanceControlTheme {
        FinanceControlLayout()
    }
}

//// PAGINA DE LOGIN / CADASTRO
//@Composable
//fun AuthFlowScreen() {
//    var showLogin by remember { mutableStateOf(true) }
//    var isLoggedIn by remember { mutableStateOf(false) }
//
//    if (isLoggedIn) {
//        FinanceControlScreen()
//    } else {
//        if (showLogin) {
//            LoginScreen(
//                onLoginSuccess = { isLoggedIn = true },
//                onNavigateToRegister = { showLogin = false }
//            )
//        } else {
//            RegisterScreen(
//                onRegisterSuccess = { isLoggedIn = true },
//                onNavigateToLogin = { showLogin = true }
//            )
//        }
//    }
//}
//
//@Composable
//fun LoginScreen(
//    onLoginSuccess: () -> Unit,
//    onNavigateToRegister: () -> Unit
//) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var error by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Login", style = MaterialTheme.typography.headlineMedium)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Senha") },
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        if (error.isNotEmpty()) {
//            Text(error, color = Color.Red)
//            Spacer(modifier = Modifier.height(8.dp))
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                // Simples teste
//                if (email.isNotBlank() && password.isNotBlank()) {
//                    onLoginSuccess()
//                } else {
//                    error = "Preencha todos os campos"
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Entrar")
//        }
//
//        TextButton(onClick = onNavigateToRegister) {
//            Text("Criar uma conta")
//        }
//    }
//}
//
//@Composable
//fun RegisterScreen(
//    onRegisterSuccess: () -> Unit,
//    onNavigateToLogin: () -> Unit
//) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//    var error by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Criar Conta", style = MaterialTheme.typography.headlineMedium)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Senha") },
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        OutlinedTextField(
//            value = confirmPassword,
//            onValueChange = { confirmPassword = it },
//            label = { Text("Confirmar Senha") },
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        if (error.isNotEmpty()) {
//            Text(error, color = Color.Red)
//            Spacer(modifier = Modifier.height(8.dp))
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//
//                when {
//                    email.isBlank() || password.isBlank() -> {
//                        error = "Preencha todos os campos"
//                    }
//                    password != confirmPassword -> {
//                        error = "As senhas não coincidem"
//                    }
//                    password.length < 6 -> {
//                        error = "Senha deve ter pelo menos 6 caracteres"
//                    }
//                    else -> {
//                        onRegisterSuccess()
//                    }
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Cadastrar")
//        }
//
//        TextButton(onClick = onNavigateToLogin) {
//            Text("Já tem uma conta? Faça login")
//        }
//    }
//}