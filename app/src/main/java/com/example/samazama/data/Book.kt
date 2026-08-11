package com.example.samazama.data

data class Book(val id: Int, val title: String, val author: String, val imageUrl: String)

val sampleBooks = listOf(
    Book(
        22563236,
        "君のクイズ",
        "小川 哲",
        "https://m.media-amazon.com/images/I/513hg7gy4rL._SL500_.jpg"
    ),
    Book(
        19991860,
        "十角館の殺人",
        "綾辻 行人",
        "https://m.media-amazon.com/images/I/41VtHAw3hyL._SL500_.jpg"
    ),
    Book(
        19991860,
        "傲慢と善良",
        "辻村 深月",
        "https://m.media-amazon.com/images/I/518ZptJK+7L._SL500_.jpg"
    ),
)