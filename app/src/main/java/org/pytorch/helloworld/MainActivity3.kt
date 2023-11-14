package org.pytorch.helloworld;

import android.os.Bundle
import android.util.Log
import org.pytorch.helloworld.model.Message
import org.pytorch.helloworld.adapter.MessageAdapter
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity3 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvWelcome: TextView
    private lateinit var etMsg: EditText
    private lateinit var btnSend: ImageButton

    private var messageList = mutableListOf<Message>()
    private lateinit var messageAdapter: MessageAdapter



    private val client = OkHttpClient()
    private val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

    companion object {
        private const val MY_SECRET_KEY = "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"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        recyclerView = findViewById(R.id.recycler_view)
        tvWelcome = findViewById(R.id.tv_welcome)
        etMsg = findViewById(R.id.et_msg)
        btnSend = findViewById(R.id.btn_send)

        recyclerView.setHasFixedSize(true)
        val manager = LinearLayoutManager(this).apply { stackFromEnd = true }
        recyclerView.layoutManager = manager

        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter

        btnSend.setOnClickListener {
            val question = etMsg.text.toString().trim()
            addToChat(question, Message.SENT_BY_ME)
            etMsg.text.clear()
            callAPI(question)
            tvWelcome.visibility = View.GONE
        }
    }

    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            messageList.add(Message(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun addResponse(response: String) {
        messageList.removeAt(messageList.size - 1)
        addToChat(response, Message.SENT_BY_BOT)
    }

    private fun callAPI(question: String) {
        messageList.add(Message("...", Message.SENT_BY_BOT))

        val jsonObject = JSONObject().apply {
            try {
                put("model", "gpt-3.5-turbo")
                // Create a JSONArray with a single JSONObject representing the message
                val messagesArray = JSONArray().apply {
                    val messageObject = JSONObject().apply {
                        put("role", "user")
                        put("content", question)
                    }
                    // Add the message object to the messages array
                    put(messageObject)

                }
                // Put the messages array into the main JSON object
                put("messages", messagesArray)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val body = jsonObject.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $MY_SECRET_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("Failed to load response due to ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    try {
                        val jsonObject = JSONObject(responseBody ?: "")
                        val jsonArray = jsonObject.getJSONArray("choices")
                        val result = jsonArray.getJSONObject(0).getString("text")

                        addResponse(result.trim())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    addResponse("Failed to load response due to ${response.body?.string()}")
                }
            }
        })
    }
}
