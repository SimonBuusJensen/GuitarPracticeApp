package com.example.simonsguitarapp


class Utils {

    companion object Factory {
        fun createChordStringToChordImageHashMap(): HashMap<String, Int> {
            return hashMapOf(
                "A" to R.drawable.a,
                "B" to R.drawable.b,
                "C" to R.drawable.c,
                "D" to R.drawable.d,
                "E" to R.drawable.e,
                "F" to R.drawable.f,
                "G" to R.drawable.g,
                "Am" to R.drawable.am,
                "Bm" to R.drawable.bm,
                "Cm" to R.drawable.cm,
                "Dm" to R.drawable.dm,
                "Em" to R.drawable.em,
                "Fm" to R.drawable.fm,
                "Gm" to R.drawable.gm,
                "A7" to R.drawable.a7,
                "B7" to R.drawable.b7,
                "C7" to R.drawable.c7,
                "D7" to R.drawable.d7,
                "E7" to R.drawable.e7,
                "F7" to R.drawable.f7,
                "G7" to R.drawable.g7,
                "Am7" to R.drawable.am7
            )
        }
    }
}