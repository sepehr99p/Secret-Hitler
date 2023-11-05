package com.example.secrethitler.ui.utils

import android.view.View
import java.security.AccessController.getContext




object ViewHelper {


    fun View.hide() {
        this.visibility = View.GONE
    }

    fun View.show() {
        this.visibility = View.VISIBLE
    }

    fun View.invisible() {
        this.visibility = View.INVISIBLE
    }

}