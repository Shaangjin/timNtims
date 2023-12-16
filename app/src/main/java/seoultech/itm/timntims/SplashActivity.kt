package seoultech.itm.timntims

import android.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import seoultech.itm.timntims.sign.SignInActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(seoultech.itm.timntims.R.layout.activity_splash)

        Handler().postDelayed({
            val intent = Intent(applicationContext, SignInActivity::class.java) //new Intent(current context, activity to be moved)
            startActivity(intent)
            finish()
        }, 1200L) // delay with 1.2 seconds
    }

}