package com.example.myapplication

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
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
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    lateinit var db: AppDatabase
    lateinit var kanjiDao: KanjiDao


    private val mutex = Mutex()
    var chosenCard = 0
    var furiganaTextViews = mutableListOf<FuriganaTextView>()
    var translationTextViews = mutableListOf<TextView>()
    var initialPosition = 0
    var initialTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(applicationContext)

        kanjiDao = db.kanjiDao()

        loadDataAndUpdateUI(getCard())


        val kanjiCharacter = findViewById<TextView>(R.id.kanji_character)

        kanjiCharacter.setOnClickListener {
            // Здесь ваш код, который будет выполняться при нажатии на элемент
            showHint()
        }

        val myScrollView: ScrollView = findViewById(R.id.scrollView)



        val group2Button = findViewById<Button>(R.id.group2_button)
        val group3Button = findViewById<Button>(R.id.group3_button)
        val group4Button = findViewById<Button>(R.id.group4_button)
        val group5Button = findViewById<Button>(R.id.group5_button)


        val menuButton = findViewById(R.id.menu_button) as ImageButton
        menuButton.setOnClickListener {
            showColorDialog()
        }

        group2Button.setOnClickListener { handleButtonClick(2) }
        group3Button.setOnClickListener { handleButtonClick(3) }
        group4Button.setOnClickListener { handleButtonClick(4) }
        group5Button.setOnClickListener { handleButtonClick(5) }

        myScrollView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Запоминаем положение при нажатии
                    initialPosition = myScrollView.scrollY
                    initialTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP -> {
                    // Проверяем, совпадает ли положение при нажатии с положением при отжатии
                    if (myScrollView.scrollY == initialPosition && System.currentTimeMillis() - initialTime <= 400) {
                        // Если совпадают, выполняем нашу функцию
                        showHint()
                    }
                }
            }
            v?.onTouchEvent(event) ?: true
        }
    }


    fun loadDataAndUpdateUI(id: Int) {
        lifecycleScope.launch {
            val kanji = withContext(Dispatchers.IO) {
                loadData(id)
            }
            updateUI(kanji)
        }
    }

    fun updateUI(kanji: Kanji) {


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

        val color = when (getColor()) {
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

        onReading.text = kanji.onyomi?.replace(",", ",\n")
        kunReading.text = kanji.kunyomi?.replace(",", ",\n")
        kanjiCharacter.text = kanji.kanji
        kanjiDefinitions.text = kanji.meaning

        onReading.setTextColor(ContextCompat.getColor(this, R.color.white))
        kunReading.setTextColor(ContextCompat.getColor(this, R.color.white))
        kanjiDefinitions.setTextColor(ContextCompat.getColor(this, R.color.white))

        // Clear any existing views in the examplesLayout
        examplesLayout.removeAllViews()


        val furigana = kanji.furigana?.split(",") ?: emptyList()
        val translations = kanji.translate?.split("\\")?: emptyList()

        furiganaTextViews.clear()
        translationTextViews.clear()

        for (i in furigana.indices) {
            val furiganaText = FuriganaTextView(this)
            val translationText = TextView(this)
            furiganaText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            furiganaText.setFuriganaText("  " + furigana[i], hasRuby = true)
            furiganaText.setTextColor(ContextCompat.getColor(this, R.color.black))
            furiganaText.furiganaTextColor = ContextCompat.getColor(this, R.color.white)

            translationText.text = " - " + translations[i].trim { it == '\n' || it <= ' ' }
            translationText.setTextColor(ContextCompat.getColor(this, R.color.white))
            translationText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            translationText.gravity = Gravity.BOTTOM

            furiganaTextViews.add(furiganaText)
            translationTextViews.add(translationText)

            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.gravity = Gravity.CENTER_VERTICAL
            linearLayout.addView(furiganaText)
            linearLayout.addView(translationText)

            examplesLayout.addView(linearLayout)
        }
    }

    private fun handleButtonClick(groupNumber: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            mutex.withLock {
                updateKanjiGroopAndPlace(kanjiDao, getCard(), groupNumber)
                if (getMode() == 0){

                    withContext(Dispatchers.Main) {
                        findMinPlaceKanji(kanjiDao, select())?.let { chosenCard = it }

                        while (chosenCard == getLastCard() || chosenCard == getPenultimateCard()) {
                            findMinPlaceKanji(kanjiDao, select())?.let { chosenCard = it }
                        }
                        setColor(findGroupByCard(kanjiDao, chosenCard))
                        loadDataAndUpdateUI(chosenCard)
                        setPenultimateCard(getLastCard())
                        setLastCard(getCard())
                        setCard(chosenCard)
                    }
                    return@withLock
                }

                val minPlaceId = findMinPlaceKanji(kanjiDao, getMode())

                withContext(Dispatchers.Main) {
                    if (minPlaceId != null) {
                        setColor(findGroupByCard(kanjiDao, minPlaceId))
                        loadDataAndUpdateUI(minPlaceId)
                        setCard(minPlaceId)

                    } else {
                        val groopWithMinPlace = kanjiDao.findGroopWithMinPlace()

                        if (groopWithMinPlace != null) {
                            setMode(groopWithMinPlace.groop_id)
                            setColor(groopWithMinPlace.groop_id)
                            setCard(groopWithMinPlace.place)
                            loadDataAndUpdateUI(groopWithMinPlace.place)
                        } else {
                            // Обрабатываем ситуацию, когда в базе данных нет записей с ненулевым местом
                        }
                    }
                }
            }
        }
    }

    fun setMode(Mode: Int) {
        // Сохранить данные
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putInt("1", Mode)
        myEdit.apply()
    }
    fun getMode(): Int{
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

    fun setColor(Color: Int) {
        // Сохранить данные
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putInt("6", Color)
        myEdit.apply()
    }
    fun getColor(): Int{
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getInt("6", 1)
    }


    fun setLastCard(card: Int) {
        // Сохранить данные
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putInt("3", card)
        myEdit.apply()
    }
    fun getLastCard(): Int{
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getInt("3", 1)
    }
    fun setPenultimateCard(card: Int) {
        // Сохранить данные
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putInt("4", card)
        myEdit.apply()
    }
    fun getPenultimateCard(): Int{
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getInt("4", 1)
    }

    suspend fun loadData(id: Int): Kanji {

        return kanjiDao.getKanjiById(id)
    }
    suspend fun updateKanjiGroopAndPlace(kanjiDao: KanjiDao, id: Int, newGroopId: Int) {
        kanjiDao.updateGroopId(id, newGroopId)
        val maxPlace = kanjiDao.getMaxPlace(newGroopId)
        kanjiDao.updatePlace(id, maxPlace?.plus(1) ?: 0)
    }

    suspend fun findMinPlaceKanji(kanjiDao: KanjiDao, groopId: Int): Int? {
        return kanjiDao.findMinPlaceKanji(groopId)
    }

    suspend fun findGroupByCard(kanjiDao: KanjiDao, cardId: Int): Int {
        return kanjiDao.getGroopId(cardId)
    }
    suspend fun getKanjiCount(groopIds: List<Int>): Map<Int, Int> {

        return withContext(Dispatchers.IO) {
            val counts = AppDatabase.getInstance(applicationContext).kanjiDao().getCountsWhereGroopIdIn(groopIds)
            groopIds.associateWith { groopId -> counts.find { it.groop_id == groopId }?.count ?: 0 }
        }
    }


    suspend fun select(): Int {
        val baseWeights = listOf(0.0, 2.0, 1.0, 0.8)
        val elementsForZeroChanceDict = mapOf(0.0 to 0, 2.0 to 5, 1.0 to 10, 0.8 to 15)

        val groopIds = listOf(1, 2, 3, 4)
        val elementsMap = getKanjiCount(groopIds)
        Log.d("67567567", elementsMap.toString())
        val elements = groopIds.map { groopId -> elementsMap[groopId] ?: 0 }
        val groups = baseWeights.zip(elements)

        if (elements.sum() == 0) {
            return 5
        }

        val totalElements = groups.subList(1, groups.size).sumOf { (baseWeight, element) -> element / elementsForZeroChanceDict[baseWeight]!! }
        val totalWeight = groups.subList(1, groups.size).sumOf { (baseWeight, element) -> baseWeight * element }

        val probGroup1 = if (groups[0].second == 0 || totalElements > 1.0) 0.0 else 1.0 - totalElements


        val choice1 = Random.nextDouble()

        if (choice1 < probGroup1) {
            return 1
        }

        var choice2 = Random.nextDouble() * totalWeight

        for (group in groups.subList(1, groups.size)) {
            if (choice2 < group.first * group.second) {
                return groups.indexOf(group) + 1
            }
            choice2 -= group.first * group.second
        }

        return 4
    }
    fun showHint() {
        // Сохранить данные
        val onReading = findViewById<TextView>(R.id.kanji_on_reading)
        val kunReading = findViewById<TextView>(R.id.kanji_kun_reading)
        val kanjiDefinitions = findViewById<TextView>(R.id.kanji_definitions)

        onReading.setTextColor(ContextCompat.getColor(this, R.color.black))
        kunReading.setTextColor(ContextCompat.getColor(this, R.color.black))
        kanjiDefinitions.setTextColor(ContextCompat.getColor(this, R.color.black))
        changeFuriganaTextColor()


    }

    fun changeFuriganaTextColor() {
        for (textView in furiganaTextViews) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.black))
            textView.furiganaTextColor = ContextCompat.getColor(this, R.color.black)
        }

        for (textView in translationTextViews) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
    }

    fun setupButton(dialog: Dialog, buttonId: Int, mode: Int? = null) {
        val button = dialog.findViewById<Button>(buttonId)
        button.setOnClickListener {
            mode?.let { setMode(it) }

            CoroutineScope(Dispatchers.IO).launch {
                val minPlaceId: Int?

                if (mode != null) {
                    minPlaceId = findMinPlaceKanji(kanjiDao, getMode())
                } else {
                    Log.d("ыфв", select().toString())
                    chosenCard = findMinPlaceKanji(kanjiDao, select())?: 0
                    Log.d("номер карты", chosenCard.toString())

                    while ((chosenCard == getLastCard() || chosenCard == getPenultimateCard()) && findGroupByCard(kanjiDao,chosenCard) != 1) {
                        chosenCard = findMinPlaceKanji(kanjiDao, select())?: 0
                    }

                    minPlaceId = chosenCard
                }

                withContext(Dispatchers.Main) {
                    if (minPlaceId != null) {
                        Log.d("1", chosenCard.toString())
                        setColor(findGroupByCard(kanjiDao, minPlaceId))
                        loadDataAndUpdateUI(minPlaceId)
                        setPenultimateCard(getLastCard())
                        setLastCard(getCard())
                        setCard(minPlaceId)
                        dialog.dismiss()
                        // Update UI
                    } else {
                        // Handle case where no records in DB
                    }
                }
            }
        }
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

        setupButton(dialog, R.id.grey_button, 1)
        setupButton(dialog, R.id.red_button, 2)
        setupButton(dialog, R.id.orange_button, 3)
        setupButton(dialog, R.id.green_button, 4)
        setupButton(dialog, R.id.blue_button, 5)
        setupButton(dialog, R.id.purple_button)
        dialog.show()
    }

}

