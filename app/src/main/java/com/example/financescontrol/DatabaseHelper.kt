import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "financas.db"
        private const val DATABASE_VERSION = 1

        // Tabela de transações
        const val TABLE_TRANSACOES = "transacoes"
        const val COLUMN_ID = "id"
        const val COLUMN_DESCRICAO = "descricao"
        const val COLUMN_VALOR = "valor"
        const val COLUMN_TIPO = "tipo" // "receita" ou "despesa"
        const val COLUMN_DATA = "data"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_TRANSACOES = """
            CREATE TABLE $TABLE_TRANSACOES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DESCRICAO TEXT,
                $COLUMN_VALOR REAL,
                $COLUMN_TIPO TEXT,
                $COLUMN_DATA TEXT
            )
        """.trimIndent()

        db.execSQL(CREATE_TABLE_TRANSACOES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACOES")
        onCreate(db)
    }

    //  adicionar uma transação
    fun addTransacao(descricao: String, valor: Double, tipo: String, data: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DESCRICAO, descricao)
            put(COLUMN_VALOR, valor)
            put(COLUMN_TIPO, tipo)
            put(COLUMN_DATA, data)
        }

        val id = db.insert(TABLE_TRANSACOES, null, values)
        db.close()
        return id
    }

    // obter todas as transações
    fun getAllTransacoes(): List<Transacao> {
        val transacoes = mutableListOf<Transacao>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TRANSACOES ORDER BY $COLUMN_DATA DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val transacao = Transacao(
                    id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    descricao = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRICAO)),
                    valor = cursor.getDouble(cursor.getColumnIndex(COLUMN_VALOR)),
                    tipo = cursor.getString(cursor.getColumnIndex(COLUMN_TIPO)),
                    data = cursor.getString(cursor.getColumnIndex(COLUMN_DATA))
                            transacoes.add(transacao)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return transacoes
    }

    //  calcular o saldo total
    fun getSaldo(): Double {
        var saldo = 0.0
        val db = this.readableDatabase

        // Soma
        val cursorReceitas = db.rawQuery("SELECT SUM($COLUMN_VALOR) FROM $TABLE_TRANSACOES WHERE $COLUMN_TIPO = 'receita'", null)
        if (cursorReceitas.moveToFirst()) {
            saldo += cursorReceitas.getDouble(0)
        }
        cursorReceitas.close()

        // Subtrai
        val cursorDespesas = db.rawQuery("SELECT SUM($COLUMN_VALOR) FROM $TABLE_TRANSACOES WHERE $COLUMN_TIPO = 'despesa'", null)
        if (cursorDespesas.moveToFirst()) {
            saldo -= cursorDespesas.getDouble(0)
        }
        cursorDespesas.close()

        db.close()
        return saldo
    }
}