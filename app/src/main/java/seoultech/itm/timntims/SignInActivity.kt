package seoultech.itm.timntims

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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

        emailLoginBtn  = findViewById(R.id.emailLoginBtn)
        emailEdit = findViewById(R.id.emailEdit)
        pwdEdit = findViewById(R.id.pwdEdit)
        signUpBtn = findViewById(R.id.SignUpBtn)

        emailLoginBtn.setOnClickListener {
            signIn(emailEdit.text.toString(), pwdEdit.text.toString())
        }

        signUpBtn.setOnClickListener {
            Log.d("ITM", "signupclick")
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            Log.d("ITM", "after")
        }
    }

    private fun moveHomePage(user: FirebaseUser?) {
        if(user != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun signIn(email: String, password: String) {
        if(email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(this, "Please write Email or Password.", Toast.LENGTH_SHORT).show()
        } else {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Toast.makeText(this,"Sign In!",Toast.LENGTH_SHORT).show()
                    moveHomePage(task.result?.user)
                } else {
                    Toast.makeText(this,"Please check your email or password.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

