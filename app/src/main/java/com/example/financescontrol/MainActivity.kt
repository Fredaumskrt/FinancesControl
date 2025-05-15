import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvSaldo: TextView
    private lateinit var rvTransacoes: RecyclerView
    private lateinit var btnNovaTransacao: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        tvSaldo = findViewById(R.id.tvSaldo)
        rvTransacoes = findViewById(R.id.rvTransacoes)
        btnNovaTransacao = findViewById(R.id.btnNovaTransacao)

        rvTransacoes.layoutManager = LinearLayoutManager(this)
        atualizarLista()

        btnNovaTransacao.setOnClickListener {
            mostrarDialogoNovaTransacao()
        }
    }

    private fun mostrarDialogoNovaTransacao() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_nova_transacao, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Nova Transação")
            .setView(dialogView)
            .create()

        val etDescricao = dialogView.findViewById<EditText>(R.id.etDescricao)
        val etValor = dialogView.findViewById<EditText>(R.id.etValor)
        val rgTipo = dialogView.findViewById<RadioGroup>(R.id.rgTipo)
        val btnSalvar = dialogView.findViewById<Button>(R.id.btnSalvar)

        btnSalvar.setOnClickListener {
            val descricao = etDescricao.text.toString()
            val valor = etValor.text.toString().toDoubleOrNull() ?: 0.0
            val tipo = if (rgTipo.checkedRadioButtonId == R.id.rbReceita) "receita" else "despesa" // Corrigido typo "despesa"
            val data = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            if (descricao.isNotEmpty() && valor != 0.0) {
                dbHelper.addTransacao(descricao, valor, tipo, data)
                atualizarLista()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Preencha todos os campos corretamente!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun atualizarLista() {
        val transacoes = dbHelper.getAllTransacoes()
        val adapter = TransacaoAdapter(transacoes)
        rvTransacoes.adapter = adapter

        // Atualiza o saldo total
        val saldo = dbHelper.getSaldo()
        tvSaldo.text = "Saldo: R$ %.2f".format(saldo)
    }
}