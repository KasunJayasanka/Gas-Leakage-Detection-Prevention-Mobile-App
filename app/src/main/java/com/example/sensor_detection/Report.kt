package com.example.sensor_detection

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar


class Report : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val bkBtnRpt= findViewById<ImageButton>(R.id.bk_btn_menu_rpt)
        val submit = findViewById<Button>(R.id.button2)

        val picker= findViewById<DatePicker>(R.id.datePicker)
        val end_picker= findViewById<DatePicker>(R.id.datePicker2)

        val outputView = findViewById<TextView>(R.id.textView11)

        var startDate = ""
        var endDate = ""

        val today = Calendar.getInstance()
        picker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)) {

            view, year, month, day ->
            val month = month+1
            startDate = "$year-$month-$day"
        }

        end_picker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)) {
                view, year, month, day ->
            val month = month+1
            endDate = "$year-$month-$day"
        }


        bkBtnRpt.setOnClickListener {
            val DICTIONARY = Intent(this, MainActivity::class.java)
            startActivity(DICTIONARY)
        }

        submit.setOnClickListener{
            val toast = "Retrieving data from start : $startDate end : $endDate"
            callPostApi(startDate,endDate,
                onSuccess = { data ->
                    runOnUiThread {
                        var output = formatJsonArrayToTable(data)
                        outputView.setText(output)
                        outputView.movementMethod = ScrollingMovementMethod()
                    }
                },
            onFailure = {
                println("Failed to retrieve JSON data from the API.")
            })

            Toast.makeText(this,toast,Toast.LENGTH_SHORT).show()
        }

    }



    fun callPostApi(startDate: String, endDate: String, onSuccess: (JSONArray) -> Unit, onFailure: () -> Unit) {
        val apiUrl = "https://lapshop.lk/api/report.php"

        val requestBody = """
        {
            "start_date": "$startDate",
            "end_date": "$endDate"
        }
    """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBodyObj = requestBody.toRequestBody(mediaType)

        GlobalScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(apiUrl)
                .post(requestBodyObj)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseData = response.body?.string()
                val jsonArray = JSONArray(responseData)
                onSuccess(jsonArray)
            } else {
                onFailure()
            }
        }
    }

    fun formatJsonArrayToTable(jsonArray: JSONArray): String {
        val stringBuilder = StringBuilder()

        // Add table headers
        stringBuilder.append("\n\n\n   Date".padEnd(22)).append("Value").append("\n\n")

        // Iterate over each JSON object in the array
        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val date: String = jsonObject.getString("dateTimeStamp")
            val value: String = jsonObject.getString("gasWeight")

            // Append formatted row to the table
            stringBuilder.append(date.padEnd(22)).append(value).append("\n")
        }

        return stringBuilder.toString()
    }

}