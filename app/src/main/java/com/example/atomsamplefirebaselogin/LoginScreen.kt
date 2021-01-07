package com.example.atomsamplefirebaselogin

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginScreen : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 100
    }

    private lateinit var gSignInButton: SignInButton
    private lateinit var continueAsGuestButton: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private  var isInProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_login)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        gSignInButton = findViewById<View>(R.id.signInButton) as SignInButton
        continueAsGuestButton = findViewById(R.id.continueAsGuest)
        makeUIChanges()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()

        gSignInButton.setOnClickListener{
            signIn()
        }

        continueAsGuestButton.setOnClickListener{
            val user = UserModel("guestUserId","guest@email.com","Guest")
            UserData.setUser(user)
            Toast.makeText(applicationContext,"Welcome Guest", Toast.LENGTH_SHORT).show()
            val homeScreenIntent = Intent(this, HomeScreen::class.java)
            startActivity(homeScreenIntent)
        }
    }

    private fun makeUIChanges() {
        val senFont = Typeface.createFromAsset(assets, "fonts/sen_regular.ttf")
        val senFontBold = Typeface.createFromAsset(assets, "fonts/sen_extrabold.ttf")
        continueAsGuestButton.setTextColor(getColor(R.color.secondaryAccent))
        continueAsGuestButton.textSize = 18.0F
        continueAsGuestButton.transformationMethod = null
        continueAsGuestButton.typeface = senFont
        for (i in gSignInButton.children) {
            if (i is TextView) {
                i.text = getString(R.string.googleSignIn)
                i.background = getDrawable(R.drawable.transparent)
                i.setTextColor(getColor(R.color.secondaryAccent))
                i.textSize = 18.0F
                i.typeface = senFont
            }
        }
        val headingText:TextView = findViewById<View>(R.id.headingTextLoginScreen) as TextView
        headingText.typeface = senFontBold

        val termsAndCondition:TextView = findViewById<View>(R.id.termsAndConditions) as TextView
        val termsText = getString(R.string.termsAndCondition)
        val ss = SpannableString(termsText)
        val cs1 : ClickableSpan = object: ClickableSpan(){
            override fun onClick(widget: View) {
                val tosUrl = "https://en.wikipedia.org/wiki/Terms_of_service"
                val tosIntent = Intent(Intent.ACTION_VIEW)
                tosIntent.data = Uri.parse(tosUrl)
                startActivity(tosIntent)
            }
        }
        val cs2 : ClickableSpan = object: ClickableSpan(){
            override fun onClick(widget: View) {
                val ppUrl = "https://en.wikipedia.org/wiki/Privacy_policy"
                val ppIntent = Intent(Intent.ACTION_VIEW)
                ppIntent.data = Uri.parse(ppUrl)
                startActivity(ppIntent)
            }
        }
        ss.setSpan(cs1, 40, 56, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(cs2, 59, 73, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        termsAndCondition.text = ss
        termsAndCondition.movementMethod = LinkMovementMethod.getInstance()
        termsAndCondition.typeface = senFont
        termsAndCondition.textSize = 12.0F
    }


    private fun signIn() {
        //Debug Line of Code
        //Used to just make the account selection dialog appear every time
        //And not default to previous choices
        googleSignInClient.signOut()

        //Resume
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val tag = "SIGNINTAG"

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if(task.isSuccessful){
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(tag, "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(tag, "Google sign in failed", e)
                }
            }
            else{
                Log.w(tag, "Google sign in failed => ${exception.toString()}")
            }


        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        findViewById<View>(R.id.loginProgressBar).visibility=View.VISIBLE
        isInProgress = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val tag = "FIREBASEGOOGLE"
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "signInWithCredential:success")
                    checkUser()
                } else {
                    Log.w(tag, "signInWithCredential:failure", task.exception)
                    findViewById<View>(R.id.loginProgressBar).visibility=View.GONE
                    isInProgress = false
                }
            }
    }

    private fun checkUser(){

        val currentUser = mAuth.currentUser
        val eid = currentUser?.uid.toString()
        val ref = FirebaseDatabase.getInstance().getReference("users")
        val homeScreenIntent = Intent(this, HomeScreen::class.java)
        val registerScreenIntent = Intent(this, RegisterScreen::class.java)
        ref.child(eid).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                findViewById<View>(R.id.loginProgressBar).visibility=View.GONE
                isInProgress = false
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(!isInProgress)
            return super.dispatchTouchEvent(ev)
        return true
    }
}
