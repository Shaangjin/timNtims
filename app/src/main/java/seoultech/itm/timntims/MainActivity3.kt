package seoultech.itm.timntims

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import seoultech.itm.timntims.adapter.MessageAdapter
import seoultech.itm.timntims.model.Message
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.GlobalScope


import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.Path.Companion.toPath
import seoultech.itm.timntims.adapter.MessageAdapterNormal

import seoultech.itm.timntims.databinding.ActivityMain3Binding // Use the correct import for your generated binding class
import kotlin.time.Duration.Companion.seconds

class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityMain3Binding
    private val messageList = mutableListOf<Message>() // Non-nullable list

    //GPT 톡방 연결을 위한 변수
    private val REQUEST_CODE = 1

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Extract the data from the result intent
            val data: Intent? = result.data
            val message = data?.getStringExtra("message_key")
            message?.let {
                // Use the 'message' here
                addResponse(message)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity3).apply { stackFromEnd = true }
            adapter = MessageAdapterNormal(messageList)
        }
        val message = intent.getStringExtra("message_key")
        message?.let {
            addResponse(it) // Add the received message to the chat
        }


        binding.btnsendgpt.setOnClickListener {
            // Create the intent to start MainActivity4
            val intent = Intent(this, MainActivity4::class.java)
            // Start MainActivity4 and expect a result back
            startForResult.launch(intent)
        }
        // Send button click listener
        binding.btnSend.setOnClickListener {
            val SendMessage = binding.etMsg.text.toString().trim()
            if (SendMessage.isNotEmpty()) {
                addToChat(SendMessage, Message.SENT_BY_ME)
                binding.etMsg.text.clear()

                //GPT API
                //Maybe FireBase?
                //GPT API

                binding.tvWelcome.visibility = View.GONE
            }
        }
    }



    private fun addToChat(message: String, sentBy: String) {
        // Add to chat and update UI on the main thread
        runOnUiThread {
            messageList.add(Message(message, sentBy))
            binding.recyclerView.adapter?.notifyDataSetChanged()
            binding.recyclerView.smoothScrollToPosition(messageList.size - 1)
        }
    }

    private fun addResponse(response: String) {
        // Add response to chat from gpt response
        //messageList.removeAt(messageList.size - 1) // Remove the loading message
        addToChat(response, Message.SENT_BY_BOT)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Get the valuable response of GPT from GPT Talk activity to this Activity
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("result_key") // Replace with your key
            if (result != null) {

                addResponse(result)
            }
        }
    }


}