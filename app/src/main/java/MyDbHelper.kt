import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream

class MyDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "kanji.db"
        private const val DATABASE_VERSION = 1  // Если версия вашей базы данных - 1
    }

    private val dbPath = context.applicationInfo.dataDir + "/databases/"
    private val dbFile = File(dbPath + DATABASE_NAME)
    private val assetPath = "kanji.db"
    private val buffer = ByteArray(1024)

    init {
        if (!dbFile.exists()) {
            copyDatabase(context)
        }
    }

    private fun copyDatabase(context: Context) {
        val inputStream = context.assets.open(assetPath)
        dbFile.parentFile?.mkdirs()
        val outputStream = FileOutputStream(dbFile)

        while (true) {
            val read = inputStream.read(buffer)
            if (read == -1) break
            outputStream.write(buffer, 0, read)
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // No need to write any code here, as we're using a pre-populated database
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // No need to write any code here, as we're using a pre-populated database
    }
}
