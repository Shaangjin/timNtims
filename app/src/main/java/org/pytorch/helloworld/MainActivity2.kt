package org.pytorch.helloworld

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val buttonStartSubActivity: Button = findViewById(R.id.button)
        buttonStartSubActivity.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
    }
}
}

