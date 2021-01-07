package com.example.atomsamplefirebaselogin

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NoInternetScreen : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_no_connection)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        updateUI()
    }

    private fun updateUI(){
        val noConnectionText = findViewById<View>(R.id.noConnectionText) as TextView
        val ensureConnectionText = findViewById<View>(R.id.ensureConnectionText) as TextView
        val senFont = Typeface.createFromAsset(assets, "fonts/sen_regular.ttf")
        val senFontBold = Typeface.createFromAsset(assets, "fonts/sen_extrabold.ttf")
        noConnectionText.typeface = senFontBold
        ensureConnectionText.typeface = senFont
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}