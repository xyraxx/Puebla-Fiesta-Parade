package dev.fs.mad.game11

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.lang.Boolean
import kotlin.Exception
import kotlin.RuntimeException


@SuppressLint("CustomSplashScreen")
class LoadingActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 3000 // 2 seconds

    companion object {
        var gameURL = ""
        var appStatus = ""
        var apiResponse = ""
    }

    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        window.setFlags(1024, 1024)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_loading)
        pref = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val connectAPI: RequestQueue = Volley.newRequestQueue(this)
        val requestBody = JSONObject()
        try {
            requestBody.put("appid", "W11")
            requestBody.put("package", packageName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val endPoint =
            "https://backend.madgamingdev.com/api/gameid" + "?appid=W11&package=" + packageName

        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, endPoint, requestBody,
            { response ->
                apiResponse = response.toString()

                try {
                    val jsonData = JSONObject(apiResponse)
                    val decryptedData = Crypt.decrypt(
                        jsonData.getString("data"),
                        "21913618CE86B5D53C7B84A75B3774CD"
                    )
                    val gameData = JSONObject(decryptedData)

                    appStatus = jsonData.getString("gameKey")
                    gameURL = gameData.getString("gameURL")

                    pref.edit().putString("gameURL", gameURL).apply()

                    // Using a Handler to delay the transition to the next activity
                    Handler().postDelayed({
                        if (Boolean.parseBoolean(appStatus)) {
                            val intent = Intent(this@LoadingActivity, WVActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this@LoadingActivity, PolicyActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }, SPLASH_TIME_OUT.toLong())

                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            },
            { error ->
                Log.d("API:RESPONSE", error.toString())
            })

        connectAPI.add(jsonRequest)

    }
}