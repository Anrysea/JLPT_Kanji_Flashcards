package com.example.myapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji")
data class Kanji(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int?,
    @ColumnInfo(name = "kanji") val kanji: String?,
    @ColumnInfo(name = "onyomi") val onyomi: String?,
    @ColumnInfo(name = "kunyomi") val kunyomi: String?,
    @ColumnInfo(name = "meaning") val meaning: String?,
    @ColumnInfo(name = "examples") val examples: String?,
    @ColumnInfo(name = "furigana") val furigana: String?,
    @ColumnInfo(name = "translate") val translate: String?,
    @ColumnInfo(name = "groop_id") var groopId: Int?,
    @ColumnInfo(name = "place") var place: Int?
)
