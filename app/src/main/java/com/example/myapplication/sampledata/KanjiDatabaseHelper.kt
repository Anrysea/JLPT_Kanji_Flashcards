package com.example.myapplication
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class KanjiDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "kanji.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_KANJI_TABLE = """
            CREATE TABLE kanji (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                symbol TEXT,
                on_reading TEXT,
                meaning_ru TEXT,
                examples TEXT
            )
        """.trimIndent()
        db.execSQL(CREATE_KANJI_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS kanji")
        onCreate(db)
    }
}
