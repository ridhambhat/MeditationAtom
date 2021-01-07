package com.example.atomsamplefirebaselogin

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashScreen : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        val cm = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        val connected = capabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true
        val progressBar = findViewById<View>(R.id.progressBar)

        Handler().postDelayed({
            progressBar.visibility = View.VISIBLE
        }, 500)

        Handler().postDelayed({
            if(connected){
                if(user != null){
                    checkUser()
                }
                else{
                    val signInIntent = Intent(this, LoginScreen::class.java)
                    startActivity(signInIntent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
            }
            else{
                val noConnectionScreenIntent = Intent(this, NoInternetScreen::class.java)
                startActivity(noConnectionScreenIntent)
                finish()
            }
        }, 1500)
    }

    private fun checkUser(){
        val currentUser = mAuth.currentUser
        val eid = currentUser?.uid.toString()
        val ref = FirebaseDatabase.getInstance().getReference("users")
        val homeScreenIntent = Intent(this, HomeScreen::class.java)
        val registerScreenIntent = Intent(this, RegisterScreen::class.java)
        ref.child(eid).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(UserModel::class.java)
                    if (user != null) {
                        UserData.setUser(user)
                    }
                    Toast.makeText(applicationContext, "Welcome back ${user?.profileName}", Toast.LENGTH_SHORT).show()
                    startActivity(homeScreenIntent)
                    finish()
                }
                else{
                    startActivity(registerScreenIntent)
                    finish()
                }
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

}
