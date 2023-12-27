package dev.fs.mad.game11

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

private lateinit var policy: WebView
private lateinit var denied: Button
private lateinit var accept: Button
private lateinit var lin: LinearLayout
private lateinit var preferences: SharedPreferences

class PolicyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_policy)

        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val accepted = preferences.getBoolean("accepted", false)
        if (accepted) {
            moveToMainActivity()
        }

        policy = findViewById(R.id.policy)
        lin = findViewById(R.id.layout)
        accept = findViewById(R.id.accept)
        denied = findViewById(R.id.reject)

        policy.loadUrl("file:///android_asset/index.html")

        accept.setOnClickListener {
            val editor = preferences.edit()
            editor.putBoolean("accepted", true)
            editor.apply()
            moveToMainActivity()
        }

        denied.setOnClickListener {
            finishAffinity()
        }
    }

    private fun moveToMainActivity() {
        val intent = Intent(this@PolicyActivity, MainActivity::class.java)
        startActivity(intent)
        finish() // Finish this activity so that pressing back won't bring it back
    }
}