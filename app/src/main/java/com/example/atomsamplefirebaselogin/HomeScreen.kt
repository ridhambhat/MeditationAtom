package com.example.atomsamplefirebaselogin

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class HomeScreen : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_home)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        mAuth = FirebaseAuth.getInstance()

        println("Profile Name => ${UserData.getUser().profileName}")
        updateUI()

        val signOutButton = findViewById<View>(R.id.signOutButton)
        signOutButton.setOnClickListener{
            mAuth.signOut()
            if(UserData.getUser().id != "guestUserId")
            {
                val signInIntent = Intent(this, LoginScreen::class.java)
                startActivity(signInIntent)
            }
            finish()
        }


    }

    private fun updateUI(){
        val senFont = Typeface.createFromAsset(assets, "fonts/sen_regular.ttf")
        val senFontBold = Typeface.createFromAsset(assets, "fonts/sen_extrabold.ttf")
        findViewById<TextView>(R.id.homeTitleText).typeface = senFontBold
        findViewById<TextView>(R.id.homesubText).typeface = senFont
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}
