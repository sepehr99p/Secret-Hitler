package com.example.secrethitler.data

data class Player(
    val name : String,
    val role : ROLE
)

enum class ROLE{
    FASCIEST,
    LIBERAL,
    HITLER
}