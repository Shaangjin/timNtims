package seoultech.itm.timntims


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.MemoryFormat
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import seoultech.itm.timntims.adapter.ChatAdapter
import seoultech.itm.timntims.databinding.ActivityMain3Binding
import seoultech.itm.timntims.model.ChatItem
import seoultech.itm.timntims.model.ImageItem
import seoultech.itm.timntims.model.MessageItem
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityMain3Binding
    private val messageList = mutableListOf<ChatItem>() // Non-nullable list

    //GPT 톡방 연결을 위한 변수
    private val REQUEST_CODE = 1
    private lateinit var textSummarizer: TextSummarizer

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

        //TextSum
        textSummarizer = TextSummarizer(this)

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val databaseRef: DatabaseReference = database.reference.child("firstmessage/WO46NUqPhqXM1Yt9hQ6TtC3okEZ2/") // "path_to_your_data"를 적절한 경로로 변경

        val valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 데이터 변경 이벤트가 발생할 때 호출됩니다.
                for (snapshot in dataSnapshot.children) {
                    // 데이터가 String 타입인 경우에 대한 처리
                    val message = snapshot.getValue(String::class.java)
                    message?.let {
                        addToChat(it, ChatItem.TYPE_MESSAGE_RECEIVED)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리 로직
                Log.e("Firebase", "Error reading data: ${databaseError.message}")
            }
        }

        // ValueEventListener를 데이터베이스 참조에 연결
        databaseRef.addValueEventListener(valueEventListener)

        // Setup RecyclerView
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity3).apply { stackFromEnd = true }
            adapter = ChatAdapter(this@MainActivity3, messageList)
        }
        val message = intent.getStringExtra("message_key")
        message?.let {
            addResponse(it) // Add the received message to the chat
        }

        //Intent
        binding.btnsendgpt.setOnClickListener {
            // Create the intent to start MainActivity4
            val intent = Intent(this, MainActivity4::class.java)
            // Start MainActivity4 and expect a result back
            startForResult.launch(intent)
        }

        binding.btnsendcalendar.setOnClickListener {
            // Create the intent to start MainActivity4
            val intent = Intent(this, CalendarActivity::class.java)
            // Start MainActivity4 and expect a result back
            startForResult.launch(intent)
        }

        // Send button click listener
        binding.btnsend.setOnClickListener {
            val SendMessage = binding.etMsg.text.toString().trim()
            if (SendMessage.isNotEmpty()) {

                //Image Upload
                //일단 임시적으로 아래 코드 사용
                if(SendMessage == "image.jpg"){
                    addToChat(SendMessage, ChatItem.TYPE_IMAGE_SENT)

                    //val resultClass = deepLearning.inference(SendMessage)
                    //showResultToast(resultClass)

                }
                //Summarize Function
                else if(SendMessage =="summarize"){
                    summarizeGptResponse()

                }
                /*else if(SendMessage.split(":")[0] =="organization") {

                        // Load the organization name finder model
                    val nameFinderModel: TokenNameFinderModel // Declare the variable in an accessible scope

                    try {
                        // Load the organization name finder model
                        this.assets.open("en-ner-organization.bin").use { modelIn ->
                            nameFinderModel = TokenNameFinderModel(modelIn)
                        }

                        val nameFinder = NameFinderME(nameFinderModel)

                        val tokenizer = SimpleTokenizer.INSTANCE
                        val tokens = tokenizer.tokenize(SendMessage)

                        // Find names within the tokenized text
                        val nameSpans = nameFinder.find(tokens)

                        // Extract and return the organization names found in the text
                        val names = nameSpans.map { span -> tokens.slice(span.getStart()..span.getEnd() - 1).joinToString(" ") }.toTypedArray()

                        // Do something with the extracted names
                        // For example, you could join them into a string and show a toast or update the UI
                        val namesString = names.joinToString(", ")
                        //addResponse(namesString)

                    } catch (e: IOException) {
                        // Handle the exception
                        Log.e("MainActivity", "Error loading the name finder model", e)
                    }

                }
                */

                else{
                    addToChat(SendMessage, ChatItem.TYPE_MESSAGE_SENT)

                    //sadasd
                    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                    val databaseReference: DatabaseReference = database.reference
                    val auth = Firebase.auth

                    val currentUserID = auth.currentUser?.uid
                    SendMessage
//                    val currentTimeInMillis = System.currentTimeMillis()


//                    databaseReference.child("firstmessage/$currentUserID/").setValue(SendMessage)
                    val newChildRef = databaseReference.child("firstmessage/WO46NUqPhqXM1Yt9hQ6TtC3okEZ2/").push()
                    newChildRef.setValue(SendMessage)
//                    databaseReference.child("chat_members/").setValue(currentUserID)
                }


                binding.etMsg.text.clear()

                //val ChatItem.TYPE_MESSAGE_SENT = 0
                //val ChatItem.TYPE_MESSAGE_RECEIVED = 1
                //val ChatItem.TYPE_IMAGE_SENT = 2
                //val ChatItem.TYPE_IMAGE_RECEIVED = 3

                //GPT API
                //Maybe FireBase?
                //GPT API

                binding.tvWelcome.visibility = View.GONE
            }
        }
    }

    private fun showResultToast(result: String) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }

    private fun summarizeGptResponse(){
        var summarizedGptResponse = ""
        messageList.forEach{
            if(it.getType() == ChatItem.TYPE_MESSAGE_RECEIVED ){
                var obj =  it as MessageItem
                summarizedGptResponse =  summarizedGptResponse +it.getMessage()
            }
        }
        val summary = textSummarizer.summarize(summarizedGptResponse, 3)
        addResponse(summary)

    }
    private fun addToChat(message: String, sentBy: Int) {
        // Add to chat and update UI on the main thread
        runOnUiThread {

            if(sentBy ==ChatItem.TYPE_MESSAGE_SENT){
                messageList.add(MessageItem(message, sentBy))

            }else if(sentBy ==ChatItem.TYPE_MESSAGE_RECEIVED){
                messageList.add(MessageItem(message, sentBy))

            }else if(sentBy ==ChatItem.TYPE_IMAGE_SENT){
                messageList.add(ImageItem(message, sentBy))


                //Deep learning Image Classification
                imageClassification(message)


            }
            else{
                messageList.add(ImageItem(message, sentBy))
                imageClassification(message)
            }

            binding.recyclerView.adapter?.notifyDataSetChanged()
            binding.recyclerView.smoothScrollToPosition(messageList.size - 1)
        }
    }
    private fun imageClassification(message:String){
        var bitmap: Bitmap? = null
        var module: Module? = null
        try {
            // creating bitmap from packaged into app android asset 'image.jpg',
            // app/src/main/assets/image.jpg
            bitmap = BitmapFactory.decodeStream(assets.open(message))
            // loading serialized torchscript module from packaged into app android asset model.pt,
            // app/src/model/assets/model.pt
            module = LiteModuleLoader.load(MainActivity.assetFilePath(this, "model.pt"))
        } catch (e: IOException) {
            Log.e("PytorchHelloWorld", "Error reading assets", e)
            finish()
        }

        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB,
            MemoryFormat.CHANNELS_LAST
        )

        val outputTensor = module!!.forward(IValue.from(inputTensor)).toTensor()

        // getting tensor content as java array of floats
        val scores = outputTensor.dataAsFloatArray

        // searching for the index with maximum score
        var maxScore = -Float.MAX_VALUE
        var maxScoreIdx = -1
        for (i in scores.indices) {
            if (scores[i] > maxScore) {
                maxScore = scores[i]
                maxScoreIdx = i
            }
        }
        val className = ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx]
        showResultToast(className)
    }
    private fun addResponse(response: String) {
        // Add response to chat from gpt response
        //messageList.removeAt(messageList.size - 1) // Remove the loading message
        addToChat(response, ChatItem.TYPE_MESSAGE_RECEIVED)
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
    companion object {
        /**
         * Copies specified asset to the file in /files app directory and returns this file absolute path.
         *
         * @return absolute file path
         */
        @Throws(IOException::class)
        fun assetFilePath(context: Context, assetName: String?): String {
            val file = File(context.filesDir, assetName)
            if (file.exists() && file.length() > 0) {
                return file.absolutePath
            }
            context.assets.open(assetName!!).use { `is` ->
                FileOutputStream(file).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (`is`.read(buffer).also { read = it } != -1) {
                        os.write(buffer, 0, read)
                    }
                    os.flush()
                }
                return file.absolutePath
            }
        }
    }

}