package seoultech.itm.timntims

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEdit: EditText
    private lateinit var pwdEdit1: EditText
    private lateinit var pwdEdit2: EditText
    private lateinit var signUpBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth

        emailEdit = findViewById(R.id.emailEdit)
        pwdEdit1 = findViewById(R.id.pwdEdit1)
        pwdEdit2 = findViewById(R.id.pwdEdit2)
        signUpBtn = findViewById(R.id.SignUpBtn)

        signUpBtn.setOnClickListener {
            signUp(emailEdit.text.toString(), pwdEdit1.text.toString())
            finish()
        }
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this,"Registration Succeeds.",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"An account already exists or registration failed.",Toast.LENGTH_SHORT).show()
                }
            }
    }
}