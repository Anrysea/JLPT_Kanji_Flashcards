package com.example.myapplication

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.content.ContentValues
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val kanjiData = "小\\ショウ,ちい(さい),こ\\маленький, малый\\小さい(маленький), 小学校(начальная школа), 小説(роман), 小鳥(птичка)"
        val parts = kanjiData.split("\\")

        val symbol = parts[0].trim()
        val onReading = parts[1].trim()
        val meaningRu = parts[2].trim()
        val examples = parts[3].trim()



        // Здесь вы можете устанавливать кандзи и описание динамически
        binding.kanjiText.text = "漢字"
        binding.kanjiDescription.text = "описание, значение"


        val word1 = findViewById(R.id.word1) as FuriganaTextView?
        word1!!.setFuriganaText("通<ruby>学<rt>がく</rt></ruby> (путь в школу)")

        val word2 = findViewById(R.id.word2) as FuriganaTextView?
        word2!!.setFuriganaText("通<ruby>信<rt>しん</rt></ruby> (связь)")

        val word3 = findViewById(R.id.word3) as FuriganaTextView?
        word3!!.setFuriganaText("通<ruby>り<rt>とお</rt></ruby> (улица)")

        val word4 = findViewById(R.id.word4) as FuriganaTextView?
        word4!!.setFuriganaText("通<ruby>常<rt>じょう</rt></ruby> (обычный)")
    }
}