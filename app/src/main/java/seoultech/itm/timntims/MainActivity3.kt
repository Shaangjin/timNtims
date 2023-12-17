package seoultech.itm.timntims


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.MemoryFormat
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import seoultech.itm.timntims.adapter.ChatAdapter
import seoultech.itm.timntims.calendar.LocalCalendarActivity
import seoultech.itm.timntims.databinding.ActivityMain3Binding
import seoultech.itm.timntims.model.ChatItem
import seoultech.itm.timntims.model.ImageItem
import seoultech.itm.timntims.model.MessageItem
import seoultech.itm.timntims.model.MessageOnFirebase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*




class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityMain3Binding
    private val messageList = mutableListOf<ChatItem>() // Non-nullable list
    private lateinit var  imageHandler:ImageHandler
    private var initialDataLoaded = false
    private var messageCount = 0
    private var messagesProcessed = 0
    private lateinit var imgName: String
    private lateinit var className:String
    private var newMessageCount = 0
    private lateinit var chatId: String

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseReference: DatabaseReference = database.reference

    //GPT 톡방 연결을 위한 변수
    private val REQUEST_CODE = 1
    private val PICK_IMAGE_REQUEST = 2
    private lateinit var textSummarizer: TextSummarizer

    private lateinit var startForResult : ActivityResultLauncher<Intent>
//    = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            // Extract the data from the result intent
//            val data: Intent? = result.data
//            val message = data?.getStringExtra("message_key")
//            message?.let {
//                // Use the 'message' here
//                addResponse(message)
//
//                val currentTimeInMillis = getCurrentTimeString()
//                databaseReference.child("messages/$chatId/${currentTimeInMillis}/").setValue(MessageOnFirebase("GPT",message,currentTimeInMillis,"gpt", chatId))
//            }
//        }
//    }

    val auth = Firebase.auth
    val currentUserID = auth.currentUser?.uid
    private var userFirstName: String? = null

    private fun loadUserFirstName(callback: (String) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val firstNameRef = databaseReference.child("users").child(userId).child("firstName")
            firstNameRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val name = dataSnapshot.getValue(String::class.java).toString()
                        callback(name) // 콜백으로 이름 전달
                    } else {
                        Log.d("ITM", "User first name not found")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("ITM", "Error loading user first name: ${databaseError.message}")
                }
            })
        } else {
            Log.d("ITM", "User ID is null")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserFirstName { name ->
            // 이 콜백 내에서 userFirstName 사용
            userFirstName = name
            // 예: UI 업데이트 또는 다른 로직 실행
            Log.d("ITM", "User first name is: $userFirstName")
        }

        val chatId = intent.getStringExtra("chatId") ?: "0000" // Replace 'defaultRoomId' with a default value

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Extract the data from the result intent
                val data: Intent? = result.data
                val message = data?.getStringExtra("message_key")
                message?.let {
                    // Use the 'message' here
                    //addResponse(message)

                    val currentTimeInMillis = getCurrentTimeString()
                    databaseReference.child("messages/$chatId/${currentTimeInMillis}/").setValue(MessageOnFirebase("GPT",message,currentTimeInMillis,"text", chatId,"","Secretary"))
                }
            }
        }

        //TextSum
        textSummarizer = TextSummarizer(this)

        //glide
        imageHandler = ImageHandler()

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val initialLoadRef: Query = database.reference.child("messages/$chatId/")

        val initialListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //messageCount = dataSnapshot.childrenCount.toInt()

                for (snapshot in dataSnapshot.children) {
                    //messagesProcessed++

                    //if (messagesProcessed < messageCount) { // Skip the last messageg
                        val message = snapshot.getValue(MessageOnFirebase::class.java)
                        message?.let {
                            it.contents?.let { contents ->
                                if (it.authorID != currentUserID && it.dataType=="text"){
                                    addToChat(contents, ChatItem.TYPE_MESSAGE_RECEIVED,it.authorName)
                                }else if(it.authorID == currentUserID && it.dataType=="text"){
                                    addToChat(contents, ChatItem.TYPE_MESSAGE_SENT,it.authorName)
                                }else if(it.authorID == currentUserID && it.dataType=="img"){
                                    messageList.add(ImageItem(it.contents, ChatItem.TYPE_IMAGE_SENT,it.authorName))
                                    binding.recyclerView.adapter?.notifyDataSetChanged()
                                    binding.recyclerView.smoothScrollToPosition(messageList.size - 1)
                                    //imageClassification(imageUri)
                                }else if(it.authorID != currentUserID && it.dataType=="img"){
                                    messageList.add(ImageItem(it.contents, ChatItem.TYPE_IMAGE_RECEIVED,it.authorName))
                                    binding.recyclerView.adapter?.notifyDataSetChanged()
                                    binding.recyclerView.smoothScrollToPosition(messageList.size - 1)
                                    //imageClassification(imageUri)
                            } else {

                                }
                                }
                            }
                    //}
                }

                initialDataLoaded = true
                initialLoadRef.removeEventListener(this)
                listenForNewMessages(chatId)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error reading data: ${databaseError.message}")
            }
        }

        initialLoadRef.addListenerForSingleValueEvent(initialListener)


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

        binding.uploadIMG.setOnClickListener {
            openGalleryForImage()
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
//            val intent = Intent(this, LocalCalendarActivity::class.java)
            val intent = Intent(this, LocalCalendarActivity::class.java).apply {
                putExtra("chatId", chatId)
                Log.d("ddddd", "$chatId in chatroom")
            }
            startActivity(intent)
            // Start MainActivity4 and expect a result back

        }

        binding.share.setOnClickListener{
            val currentTimeInMillis = getCurrentTimeString()
            databaseReference.child("messages/$chatId/${currentTimeInMillis}/").setValue(MessageOnFirebase(currentUserID,imgName,currentTimeInMillis,"img", chatId,className, userFirstName))
            //messageList.add(ImageItem(imageUri, ChatItem.TYPE_IMAGE_SENT))
            //binding.recyclerView.adapter?.notifyDataSetChanged()
            //binding.recyclerView.smoothScrollToPosition(messageList.size - 1)
            //imageClassification(imageUri)

        }


        // Send button click listener
        binding.btnsend.setOnClickListener {
            val SendMessage = binding.etMsg.text.toString().trim()
            if (SendMessage.isNotEmpty()) {

                //Summarize Function
                if(SendMessage =="summarize"){
                    summarizeGptResponse(chatId)

                }else if (SendMessage=="glide"){
                    messageList.add(ImageItem(ChatItem.TYPE_IMAGE_RECEIVED))
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                    binding.recyclerView.smoothScrollToPosition(messageList.size - 1)
                    //imageClassification(imageUri)
                }


                else{

                    val currentTimeInMillis = getCurrentTimeString()
                    databaseReference.child("messages/$chatId/${currentTimeInMillis}/").setValue(MessageOnFirebase(currentUserID,SendMessage,currentTimeInMillis,"text", chatId,"", userFirstName))
                }


                binding.etMsg.text.clear()

            }
        }
    }

    private fun showResultToast(result: String) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }
    fun getCurrentTimeString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
    private fun summarizeGptResponse(chatId: String){
        var summarizedGptResponse = ""
        messageList.forEach{
            if(it.getType() == ChatItem.TYPE_MESSAGE_RECEIVED ){
                var obj =  it as MessageItem
                summarizedGptResponse =  summarizedGptResponse +it.getMessage()
            }
        }
        val summary = textSummarizer.summarize(summarizedGptResponse, 3)
        // addResponse(summary)

        val currentTimeInMillis = getCurrentTimeString()
        databaseReference.child("messages/$chatId/${currentTimeInMillis}/").setValue(MessageOnFirebase("SummarizerDeeplearing",summary,currentTimeInMillis,"text", chatId,"", "Secretary"))
    }
    private fun addToChat(message: String, sentBy: Int,senderName: String?) {
        // Add to chat and update UI on the main thread
        runOnUiThread {

            if(sentBy ==ChatItem.TYPE_MESSAGE_SENT){
                messageList.add(MessageItem(message, sentBy, senderName))

            }else if(sentBy ==ChatItem.TYPE_MESSAGE_RECEIVED){
                messageList.add(MessageItem(message, sentBy, senderName))

            }

            binding.recyclerView.adapter?.notifyDataSetChanged()
            binding.recyclerView.smoothScrollToPosition(messageList.size - 1)
        }
    }

    private fun listenForNewMessages(chatId: String) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val newMessageRef: Query = database.reference.child("messages/$chatId/").limitToLast(1)

        val newMessageListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (initialDataLoaded) { // To ensure we skip initial data load
                    for (snapshot in dataSnapshot.children) {
                        if(newMessageCount>0){
                            val message = snapshot.getValue(MessageOnFirebase::class.java)
                            message?.let {
                                it.contents?.let { contents ->
                                    if (it.authorID != currentUserID && it.dataType == "text"){
                                        addToChat(contents, ChatItem.TYPE_MESSAGE_RECEIVED,"Secretary")
                                    }else if (it.authorID == currentUserID && it.dataType =="text"){
                                        addToChat(contents, ChatItem.TYPE_MESSAGE_SENT,it.authorName)
                                    }else if(it.authorID == currentUserID && it.dataType=="img"){
                                        messageList.add(ImageItem(it.contents, ChatItem.TYPE_IMAGE_SENT,it.authorName))
                                        binding.recyclerView.adapter?.notifyDataSetChanged()
                                        binding.recyclerView.smoothScrollToPosition(messageList.size - 1)

                                        Toast.makeText(this@MainActivity3, it.addMaterial, Toast.LENGTH_SHORT).show()

                                    }else if(it.authorID != currentUserID && it.dataType=="img"){
                                        messageList.add(ImageItem(it.contents, ChatItem.TYPE_IMAGE_RECEIVED,it.authorName))
                                        binding.recyclerView.adapter?.notifyDataSetChanged()
                                        binding.recyclerView.smoothScrollToPosition(messageList.size - 1)
                                        Toast.makeText(this@MainActivity3, it.addMaterial, Toast.LENGTH_SHORT).show()
                                    } else {

                                    }
                                }
                            }
                        }
                        newMessageCount++

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error reading data: ${databaseError.message}")
            }
        }

        newMessageRef.addValueEventListener(newMessageListener)
    }
    private fun imageClassification(uri:Uri): String {
        var bitmap: Bitmap? = null
        var module: Module? = null
        try {
            // creating bitmap from packaged into app android asset 'image.jpg',
            // app/src/main/assets/image.jpg
            bitmap = uriToBitmap(this,uri)
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
        return className
    }
    private fun addResponse(response: String) {
        // Add response to chat from gpt response
        //messageList.removeAt(messageList.size - 1) // Remove the loading message
        addToChat(response, ChatItem.TYPE_MESSAGE_RECEIVED,"Secretary")
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    fun uriToBitmap(context: Context, imageUri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            imgName = imageUri.lastPathSegment.toString()

            imageHandler.uploadImage(imageUri) { isSuccess, message ->
                runOnUiThread {
                    Toast.makeText(this@MainActivity3, message, Toast.LENGTH_SHORT).show()
                    className = imageClassification(imageUri)
                }
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