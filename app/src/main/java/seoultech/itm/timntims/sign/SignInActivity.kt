package seoultech.itm.timntims.sign

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import seoultech.itm.timntims.R
import seoultech.itm.timntims.home.HomeActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailLoginBtn: Button
    private lateinit var emailEdit: EditText
    private lateinit var pwdEdit: EditText
    private lateinit var signUpBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        auth = Firebase.auth

        if (auth.currentUser != null) { // 자동 로그인: 이미 로그인한 사용자가 있으면 HomeActivity로 이동
            moveHomeActivity()
        }

        emailLoginBtn  = findViewById(R.id.emailLoginBtn)
        emailEdit = findViewById(R.id.emailEdit)
        pwdEdit = findViewById(R.id.pwdEdit)
        signUpBtn = findViewById(R.id.SignUpBtn)

        emailLoginBtn.setOnClickListener {
            signIn(emailEdit.text.toString(), pwdEdit.text.toString())
        }

        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun moveHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun signIn(email: String, password: String) {
        if(email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(this, "Please write Email or Password.", Toast.LENGTH_SHORT).show()
        } else {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this,"Sign In!",Toast.LENGTH_SHORT).show()
                    moveHomeActivity()
                } else {
                    Toast.makeText(this,"Please check your email or password.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

