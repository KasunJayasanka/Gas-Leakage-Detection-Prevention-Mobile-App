package com.example.sensor_detection

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Dashboard : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bkBtnDash= findViewById<ImageButton>(R.id.bk_btn_menu_dash)

        val gas_view = findViewById<TextView>(R.id.textView12)
        val temp_view = findViewById<TextView>(R.id.textView13)
        val weight_view = findViewById<TextView>(R.id.weight)
        val date_view = findViewById<TextView>(R.id.textView16)
        val flame_view = findViewById<TextView>(R.id.flameText)

        val formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
        val current = LocalDateTime.now().format(formatter)
        date_view.text = current

        val window1= findViewById<TextView>(R.id.textView17)
        val window2= findViewById<TextView>(R.id.textView23)

        val apiUrl = "https://lapshop.lk/api/fetch.php"
        retrieveJsonData(
            apiUrl,
            onSuccess = { data ->
                Log.d("dev", data.toString())
                Log.d("dev temp", data.getString("temperatureValue"))
                runOnUiThread {

                    if (data.getString("gasLeakageDetected").equals("1")){
                        gas_view.setTextColor(Color.RED)
                        gas_view.text = "Gas Leakage\n Detected!"
                    }else{
                        gas_view.setTextColor(Color.GREEN)
                        gas_view.text = "No \nGas \nLeakage"
                    }

                    temp_view.text = data.getString("temperatureValue")+"C"
                    weight_view.text = data.getString("gasWeight")+"Kg"

                    if (data.getString("flameDetected").equals("1")){
                        flame_view.setTextColor(Color.RED)
                        flame_view.text = "Fire\n Detected!"

                    }else{
                        flame_view.setTextColor(Color.GREEN)
                        flame_view.text = "No \nFire Detected"
                    }

                }

                windowUpdate(data.getString("window1Status"), window1, "Window 1");
                windowUpdate(data.getString("window2Status"), window2, "Window 2");

            },
            onFailure = {
                println("Failed to retrieve JSON data from the API.")
            }
        )

        bkBtnDash.setOnClickListener {
            val DICTIONARY = Intent(this, MainActivity::class.java)
            startActivity(DICTIONARY)
        }
    }

    fun retrieveJsonData(url: String, onSuccess: (JSONObject) -> Unit, onFailure: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            val response: Response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseData = response.body?.string()
                val jsonObject = JSONObject(responseData)
                onSuccess(jsonObject)
            } else {
                onFailure()
            }
        }
    }

    fun windowUpdate(status:String, textView: TextView, name:String){
        if(status.equals("1")){
            runOnUiThread {
                textView.setText("\n$name Close")
                textView.setBackgroundColor(Color.RED)
                textView.setBackgroundResource(R.drawable.status_red)
            }
        }else{
            runOnUiThread {
                textView.setText("\n$name Open")
                textView.setBackgroundResource(R.drawable.status_green)
            }
        }
    }
}