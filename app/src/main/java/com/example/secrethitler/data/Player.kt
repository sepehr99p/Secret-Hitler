package com.example.secrethitler.data

import com.example.secrethitler.R

data class Player(
    val name : String,
    val role : ROLE
)

enum class ROLE(val color : Int) {
    FASCISM(R.color.red),
    LIBERAL(R.color.blue),
    HITLER(R.color.red),
    COMMUNISM(R.color.brown),
    STALIN(R.color.brown)
}