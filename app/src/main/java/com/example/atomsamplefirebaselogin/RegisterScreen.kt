package com.example.atomsamplefirebaselogin

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterScreen : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var profileName : EditText
    private lateinit var registerUserButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_user_registration)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        mAuth = FirebaseAuth.getInstance()
        profileName = findViewById(R.id.profileName)
        registerUserButton = findViewById(R.id.registerUser)

        updateUI()

        registerUserButton.setOnClickListener{
            registerUser()
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener{
            mAuth.signOut()
            val signInIntent = Intent(this, LoginScreen::class.java)
            startActivity(signInIntent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }

    private fun updateUI(){
        val senFont = Typeface.createFromAsset(assets, "fonts/sen_regular.ttf")
        val senFontBold = Typeface.createFromAsset(assets, "fonts/sen_bold.ttf")
        val senFontExtraBold = Typeface.createFromAsset(assets, "fonts/sen_extrabold.ttf")
        findViewById<TextView>(R.id.registerTitle).typeface = senFontExtraBold
        findViewById<EditText>(R.id.profileName).typeface = senFontBold
        registerUserButton.typeface = senFont
        registerUserButton.transformationMethod = null;
        registerUserButton.textSize = 18.0F
    }

    private fun registerUser(){
        val currentUser = mAuth.currentUser
        val email = currentUser?.email.toString()
        val eid = currentUser?.uid.toString()
        val name = profileName.text.toString()

        if(name.trim().isNotEmpty()){
            val ref = FirebaseDatabase.getInstance().getReference("users")
            val userId = ref.push().key.toString()
            val user = UserModel(userId, email, name)

            val homeScreenIntent = Intent(this, HomeScreen::class.java)

            ref.child(eid).setValue(user).addOnCompleteListener{
                Toast.makeText(applicationContext, "Welcome $name", Toast.LENGTH_SHORT).show()
                UserData.setUser(user)
                startActivity(homeScreenIntent)
                finish()
            }
        }
        else{
            profileName.error = "Nick name can't be empty"
        }


    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}
