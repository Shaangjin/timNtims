package seoultech.itm.timntims.sign

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import seoultech.itm.timntims.R

class SignUpActivity : AppCompatActivity(){
    private lateinit var auth: FirebaseAuth

    private lateinit var emailEdit: EditText
    private lateinit var pwdEdit1: EditText
    private lateinit var pwdEdit2: EditText

    private lateinit var emailText: TextView
    private lateinit var pwdText1: TextView
    private lateinit var pwdText2: TextView

    private lateinit var signUpBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth

        emailEdit = findViewById(R.id.emailEdit)
        pwdEdit1 = findViewById(R.id.pwdEdit1)
        pwdEdit2 = findViewById(R.id.pwdEdit2)

        emailText = findViewById(R.id.emailText)
        pwdText1 = findViewById(R.id.pwdText1)
        pwdText2 = findViewById(R.id.pwdText2)

        signUpBtn = findViewById(R.id.SignUpBtn)

        signUpBtn.setOnClickListener {
            val email = emailEdit.text.toString()
            val password = pwdEdit1.text.toString()

            signUp(email, password)
        }

        // emailEdit 작성 형식에 따라 emailText가 변경됨.
        emailEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isValidEmail(s.toString())) {
                    emailText.text = "Format is correct."
                } else {
                    emailText.text = "Please enter a valid email address."
                }
                updateSignUpButton()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // pwdEdit1 작성 형식에 따라 pwdText1가 변경됨.
        pwdEdit1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isValidPassword(s.toString())) {
                    pwdText1.text = "Format is correct."
                } else {
                    pwdText1.text = "At least 6 characters with numbers/alphabets are needed."
                }
                updateSignUpButton()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // pwdEdit2 작성 형식에 따라 pwdText2가 변경됨.
        pwdEdit2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isPasswordMatch(pwdEdit1.text.toString(), s.toString())) {
                    pwdText2.text = "Passwords match."
                } else {
                    pwdText2.text = "Passwords do not match."
                }
                updateSignUpButton()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    // 모든 textEdit이 올바르게 작성되었는지 확인 후, 버튼 활성화 여부를 결정하는 함수
    private fun updateSignUpButton() {
        val isEmailValid = isValidEmail(emailEdit.text.toString())
        val isPasswordValid = isValidPassword(pwdEdit1.text.toString())
        val isPasswordMatch = isPasswordMatch(pwdEdit1.text.toString(), pwdEdit2.text.toString())

        signUpBtn.isEnabled = isEmailValid && isPasswordValid && isPasswordMatch
        signUpBtn.alpha = if (signUpBtn.isEnabled) 1.0f else 0.3f
    }

    // email이 올바른 형식으로 작성되었는지 확인하는 함수
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // password가 올바른 형식으로 작성되었는지 확인하는 함수
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-z]).{6,}$" // 비밀번호가 최소 6자 이상이며, 숫자와 대소문자 영문을 포함
        return password.matches(passwordPattern.toRegex())
    }

    // 두 password가 일치하는지 확인하는 함수
    private fun isPasswordMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword // pwdEdit1과 pwdEdit2의 내용이 동일한지 확인
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Registration Succeeds.", Toast.LENGTH_SHORT).show()
                    finish() // 회원가입이 성공하면 액티비티 종료
                } else {
                    Toast.makeText(this, "An account already exists or registration failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}