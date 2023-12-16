package seoultech.itm.timntims.sign

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import seoultech.itm.timntims.MainActivity
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

        val videoView = findViewById<VideoView>(R.id.videoView)

        // 비디오 파일의 경로를 설정
        val videoPath = "android.resource://$packageName/" + R.raw.video_sign_in // 비디오 파일의 이름을 여기에 넣으세요

        // 비디오 뷰에 비디오 설정
        videoView.setVideoURI(Uri.parse(videoPath))

        // 비디오 재생 시작
        videoView.start()

        // 반복 재생
        videoView.setOnCompletionListener { mp -> mp.start() }

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

