package com.example.myapplication

import MyDbHelper
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var kanjiDao: KanjiDao

    companion object {
        private const val DATABASE_NAME = "kanji.db"
    }
    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "kanji.db").build()

        kanjiDao = db.kanjiDao()

        loadData(getCard())

        val group2Button = findViewById<Button>(R.id.group2_button)
        val group3Button = findViewById<Button>(R.id.group3_button)
        val group4Button = findViewById<Button>(R.id.group4_button)
        val group5Button = findViewById<Button>(R.id.group5_button)


        val menuButton = findViewById(R.id.menu_button) as ImageButton
        menuButton.setOnClickListener {
            showColorDialog()
        }

        group2Button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                // Запускаем updateKanjiGroopAndPlace в фоновом потоке
                updateKanjiGroopAndPlace(kanjiDao, getCard(), 2)

                // Запускаем findMinPlaceKanji в фоновом потоке и обновляем пользовательский интерфейс
                val minPlaceId = findMinPlaceKanji(kanjiDao, getGroop())

                withContext(Dispatchers.Main) {
                    if (minPlaceId != null) {
                        loadData(minPlaceId)
                        setCard(minPlaceId)
                        // обновляем пользовательский интерфейс
                    } else {
                        val groopWithMinPlace = kanjiDao.findGroopWithMinPlace()

                        if (groopWithMinPlace != null) {
                            setGroop(groopWithMinPlace.groop_id)
                            setCard(groopWithMinPlace.place)
                            loadData(groopWithMinPlace.place)
                        } else {
                            // Обрабатываем ситуацию, когда в базе данных нет записей с ненулевым местом
                        }
                    }
                }
            }
        }

        group3Button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                // Запускаем updateKanjiGroopAndPlace в фоновом потоке
                updateKanjiGroopAndPlace(kanjiDao, getCard(), 3)

                // Запускаем findMinPlaceKanji в фоновом потоке и обновляем пользовательский интерфейс
                val minPlaceId = findMinPlaceKanji(kanjiDao, getGroop())

                withContext(Dispatchers.Main) {
                    if (minPlaceId != null) {
                        loadData(minPlaceId)
                        setCard(minPlaceId)
                        // обновляем пользовательский интерфейс
                    } else {
                        val groopWithMinPlace = kanjiDao.findGroopWithMinPlace()

                        if (groopWithMinPlace != null) {
                            setGroop(groopWithMinPlace.groop_id)
                            setCard(groopWithMinPlace.place)
                            loadData(groopWithMinPlace.place)
                        } else {
                            // Обрабатываем ситуацию, когда в базе данных нет записей с ненулевым местом
                        }
                    }
                }
            }
        }

        group4Button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                // Запускаем updateKanjiGroopAndPlace в фоновом потоке
                updateKanjiGroopAndPlace(kanjiDao, getCard(), 4)

                // Запускаем findMinPlaceKanji в фоновом потоке и обновляем пользовательский интерфейс
                val minPlaceId = findMinPlaceKanji(kanjiDao, getGroop())

                withContext(Dispatchers.Main) {
                    if (minPlaceId != null) {
                        loadData(minPlaceId)
                        setCard(minPlaceId)
                        // обновляем пользовательский интерфейс
                    } else {
                        val groopWithMinPlace = kanjiDao.findGroopWithMinPlace()

                        if (groopWithMinPlace != null) {
                            setGroop(groopWithMinPlace.groop_id)
                            setCard(groopWithMinPlace.place)
                            loadData(groopWithMinPlace.place)
                        } else {
                            // Обрабатываем ситуацию, когда в базе данных нет записей с ненулевым местом
                        }
                    }
                }
            }
        }

        group5Button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                // Запускаем updateKanjiGroopAndPlace в фоновом потоке
                updateKanjiGroopAndPlace(kanjiDao, getCard(), 5)

                // Запускаем findMinPlaceKanji в фоновом потоке и обновляем пользовательский интерфейс
                val minPlaceId = findMinPlaceKanji(kanjiDao, getGroop())

                withContext(Dispatchers.Main) {
                    if (minPlaceId != null) {
                        loadData(minPlaceId)
                        setCard(minPlaceId)
                        // обновляем пользовательский интерфейс
                    } else {
                        val groopWithMinPlace = kanjiDao.findGroopWithMinPlace()

                        if (groopWithMinPlace != null) {
                            setGroop(groopWithMinPlace.groop_id)
                            setCard(groopWithMinPlace.place)
                            loadData(groopWithMinPlace.place)
                        } else {
                            // Обрабатываем ситуацию, когда в базе данных нет записей с ненулевым местом
                        }
                    }
                }
            }
        }
    }

    private fun loadData(id: Int) {
        val dbHelper = MyDbHelper(this)
        val db = dbHelper.writableDatabase

        val cursor = db.query("Kanji", null, "id=?", arrayOf(id.toString()), null, null, null)

        if (cursor.moveToFirst()) {
            val onReading = findViewById<TextView>(R.id.kanji_on_reading)
            val kunReading = findViewById<TextView>(R.id.kanji_kun_reading)
            val kanjiCharacter = findViewById<TextView>(R.id.kanji_character)
            val kanjiDefinitions = findViewById<TextView>(R.id.kanji_definitions)
            val line1 = findViewById<View>(R.id.line1)
            val line2 = findViewById<View>(R.id.line2)
            val line3 = findViewById<View>(R.id.line3)
            val line4 = findViewById<View>(R.id.line4)

            // Scroll View and its inner Linear Layout
            val scrollView = findViewById<ScrollView>(R.id.scrollView)
            val examplesLayout = scrollView.getChildAt(0) as LinearLayout

            val onyomiIndex = cursor.getColumnIndex("onyomi")
            val kunyomiIndex = cursor.getColumnIndex("kunyomi")
            val kanjiIndex = cursor.getColumnIndex("kanji")
            val meaningIndex = cursor.getColumnIndex("meaning")
            val furiganaIndex = cursor.getColumnIndex("furigana")
            val translateIndex = cursor.getColumnIndex("translate")


            val color = when (getGroop()) {
                1 -> R.color.grey
                2 -> R.color.colorRed
                3 -> R.color.colorYellow
                4 -> R.color.colorGreen
                5 -> R.color.colorBlue
                else -> R.color.grey // Fallback to grey if value is out of range
            }

            line1.setBackgroundColor(ContextCompat.getColor(this, color))
            line2.setBackgroundColor(ContextCompat.getColor(this, color))
            line3.setBackgroundColor(ContextCompat.getColor(this, color))
            line4.setBackgroundColor(ContextCompat.getColor(this, color))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window?.statusBarColor = ContextCompat.getColor(this, color)
            }

            if (onyomiIndex != -1) onReading.text = cursor.getString(onyomiIndex).replace(",", ",\n")
            if (kunyomiIndex != -1) kunReading.text = cursor.getString(kunyomiIndex).replace(",", ",\n")
            if (kanjiIndex != -1) kanjiCharacter.text = cursor.getString(kanjiIndex)
            if (meaningIndex != -1) kanjiDefinitions.text = cursor.getString(meaningIndex)

            // Clear any existing views in the examplesLayout
            examplesLayout.removeAllViews()

            if (furiganaIndex != -1 && translateIndex != -1) {
                val furigana = cursor.getString(furiganaIndex).split(",")
                val translations = cursor.getString(translateIndex).split("\\")

                for (i in furigana.indices) {
                    val furiganaText = FuriganaTextView(this)
                    val translationText = TextView(this)
                    furiganaText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
                    furiganaText.setFuriganaText("  " + furigana[i], hasRuby = true)
                    furiganaText.setTextColor(ContextCompat.getColor(this, R.color.black))
                    furiganaText.furiganaTextColor = ContextCompat.getColor(this, R.color.black)

                    translationText.text = " - " + translations[i].trim { it == '\n' || it <= ' ' }
                    translationText.setTextColor(ContextCompat.getColor(this, R.color.black))
                    translationText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    translationText.gravity = Gravity.BOTTOM

                    val linearLayout = LinearLayout(this)
                    linearLayout.orientation = LinearLayout.HORIZONTAL
                    linearLayout.gravity = Gravity.CENTER_VERTICAL
                    linearLayout.addView(furiganaText)
                    linearLayout.addView(translationText)

                    examplesLayout.addView(linearLayout)
                }
            }
        }
        cursor.close()
    }
    fun setGroop(groop: Int) {
        // Сохранить данные
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putInt("1", groop)
        myEdit.apply()
    }
    fun getGroop(): Int{
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getInt("1", 1)
    }
    fun setCard(card: Int) {
        // Сохранить данные
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putInt("2", card)
        myEdit.apply()
    }
    fun getCard(): Int{
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getInt("2", 1)
    }

    suspend fun updateKanjiGroopAndPlace(kanjiDao: KanjiDao, id: Int, newGroopId: Int) {
        kanjiDao.updateGroopId(id, newGroopId)
        val maxPlace = kanjiDao.getMaxPlace(newGroopId)
        kanjiDao.updatePlace(id, maxPlace?.plus(1) ?: 0)
    }

    suspend fun findMinPlaceKanji(kanjiDao: KanjiDao, groopId: Int): Int? {
        return kanjiDao.findMinPlaceKanji(groopId)
    }

    fun showColorDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog_layout)

        // Установка размера диалога после его отображения
        dialog.window?.let { window ->
            val displayMetrics = DisplayMetrics()
            window.windowManager.defaultDisplay.getMetrics(displayMetrics)

            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)

            val dialogWindowWidth = (displayMetrics.widthPixels * 0.8).toInt() // Width: 80% of screen width
            val dialogWindowHeight = (displayMetrics.heightPixels * 0.8).toInt() // Height: 80% of screen height

            layoutParams.width = dialogWindowWidth
            layoutParams.height = dialogWindowHeight

            window.attributes = layoutParams
        }

        val greyButton = dialog.findViewById(R.id.grey_button) as Button
        greyButton.setOnClickListener {

            setGroop(1)

            CoroutineScope(Dispatchers.IO).launch {

                val minPlaceId = findMinPlaceKanji(kanjiDao, getGroop())

                withContext(Dispatchers.Main) {
                    if (minPlaceId != null) {
                        loadData(minPlaceId)
                        setCard(minPlaceId)
                        dialog.dismiss()
                        // обновляем пользовательский интерфейс
                    } else {
                        // обрабатываем случай, когда в базе данных нет записей
                    }
                }
            }
        }
        val redButton = dialog.findViewById(R.id.red_button) as Button
        redButton.setOnClickListener {

            setGroop(2)

            CoroutineScope(Dispatchers.IO).launch {

                val minPlaceId = findMinPlaceKanji(kanjiDao, getGroop())

                withContext(Dispatchers.Main) {
                    if (minPlaceId != null) {
                        loadData(minPlaceId)
                        setCard(minPlaceId)
                        dialog.dismiss()
                        // обновляем пользовательский интерфейс
                    } else {
                        // обрабатываем случай, когда в базе данных нет записей
                    }
                }
            }
        }

        val orangeButton = dialog.findViewById(R.id.orange_button) as Button
        orangeButton.setOnClickListener {

            setGroop(3)

            CoroutineScope(Dispatchers.IO).launch {

                val minPlaceId = findMinPlaceKanji(kanjiDao, getGroop())

                withContext(Dispatchers.Main) {
                    if (minPlaceId != null) {
                        loadData(minPlaceId)
                        setCard(minPlaceId)
                        dialog.dismiss()
                        // обновляем пользовательский интерфейс
                    } else {
                        // обрабатываем случай, когда в базе данных нет записей
                    }
                }
            }
        }

        val greenButton = dialog.findViewById(R.id.green_button) as Button
        greenButton.setOnClickListener {

            setGroop(4)

            CoroutineScope(Dispatchers.IO).launch {

                val minPlaceId = findMinPlaceKanji(kanjiDao, getGroop())

                withContext(Dispatchers.Main) {
                    if (minPlaceId != null) {
                        loadData(minPlaceId)
                        setCard(minPlaceId)
                        dialog.dismiss()
                        // обновляем пользовательский интерфейс
                    } else {
                        // обрабатываем случай, когда в базе данных нет записей
                    }
                }
            }
        }

        val blueButton = dialog.findViewById(R.id.blue_button) as Button
        blueButton.setOnClickListener {

            setGroop(5)

            CoroutineScope(Dispatchers.IO).launch {

                val minPlaceId = findMinPlaceKanji(kanjiDao, getGroop())

                withContext(Dispatchers.Main) {
                    if (minPlaceId != null) {
                        loadData(minPlaceId)
                        setCard(minPlaceId)
                        dialog.dismiss()
                        // обновляем пользовательский интерфейс
                    } else {
                        // обрабатываем случай, когда в базе данных нет записей
                    }
                }
            }
        }

        dialog.show()
    }

}
