package com.example.secrethitler.ui.utils

import android.view.View

object ViewHelper {


    fun View.hide() {
        this.visibility = View.GONE
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }
}