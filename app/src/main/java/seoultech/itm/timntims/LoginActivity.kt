package seoultech.itm.timntims

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailLoginBtn: Button
    private lateinit var emailEdit: EditText
    private lateinit var pwdEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        emailLoginBtn  = findViewById(R.id.emailLoginBtn)
        emailEdit = findViewById(R.id.emailEdit)
        pwdEdit = findViewById(R.id.pwdEdit)

        emailLoginBtn.setOnClickListener {
            emailLogin()
        }
    }

    private fun emailLogin() {
        if(emailEdit.text.toString().isNullOrEmpty() || pwdEdit.text.toString().isNullOrEmpty()) {
            Toast.makeText(this, "Please write Email or Password.", Toast.LENGTH_SHORT).show()
        } else {
            signInAndSignUp()
        }
    }

    private fun signInAndSignUp() {
        auth.createUserWithEmailAndPassword(emailEdit.text.toString(), pwdEdit.text.toString()).addOnCompleteListener {  task ->
            if(task.isSuccessful) {
                moveMainPage(task.result?.user)
            } else if(task.exception?.message.isNullOrEmpty()) {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            } else {
                signInEmail()
            }
        }
    }

    private fun signInEmail() {
        auth.signInWithEmailAndPassword(emailEdit.text.toString(), pwdEdit.text.toString()).addOnCompleteListener{  task ->
            if(task.isSuccessful) {
                moveMainPage(task.result?.user)
            } else {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun moveMainPage(user: FirebaseUser?) {
        if(user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

