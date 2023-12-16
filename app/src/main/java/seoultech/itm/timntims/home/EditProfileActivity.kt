package seoultech.itm.timntims.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.yalantis.ucrop.UCrop
import seoultech.itm.timntims.R
import seoultech.itm.timntims.model.User
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editEmail: EditText
    private lateinit var imageUser: ImageButton
    private lateinit var buttonBack: Button
    private lateinit var buttonConfirm: Button

    private val IMAGE_PICK_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val firstName = sharedPreferences.getString("firstName", "")
        val lastName = sharedPreferences.getString("lastName", "")
        val email = sharedPreferences.getString("email", "")
        val profileImage = sharedPreferences.getString("profileImage", "")

        editFirstName = findViewById(R.id.editFirstName)
        editLastName = findViewById(R.id.editLastName)
        editEmail = findViewById(R.id.editEmail)
        imageUser = findViewById(R.id.imageUser)
        buttonBack = findViewById(R.id.ButtonBack)
        buttonConfirm = findViewById(R.id.ButtonConfirm)

        editFirstName.setText(firstName)
        editLastName.setText(lastName)
        editEmail.setText(email)

        Glide.with(this).load(profileImage).override(200, 200).into(imageUser)

        imageUser.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
        }

        buttonBack.setOnClickListener { finish() }
        buttonConfirm.setOnClickListener { updateUserProfile() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val sourceUri = data.data ?: return
            val destinationUri = Uri.fromFile(File(cacheDir, "cropped"))
            UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(450, 450)
                .start(this)
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK && data != null) {
            val resultUri: Uri? = UCrop.getOutput(data)
            imageUser.setImageURI(resultUri)
            selectedImageUri = resultUri
        }
    }

    private fun updateUserProfile() {
        val firstName = editFirstName.text.toString().trim()
        val lastName = editLastName.text.toString().trim()
        val email = editEmail.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Fill the name field.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isEmailValid(email)) {
            Toast.makeText(this, "Fill the correct Email.", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri != null) {
            uploadImageToFirebase(selectedImageUri!!)
        } else {
            saveUserProfileToDatabase(firstName, lastName, email, null)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("profile_images/${FirebaseAuth.getInstance().currentUser?.uid}.jpg")

        imageRef.putFile(imageUri).addOnSuccessListener {
            imageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val imageUrl = task.result.toString()
                    saveUserProfileToDatabase(editFirstName.text.toString(), editLastName.text.toString(), editEmail.text.toString(), imageUrl)
                } else {
                    Toast.makeText(this, "Fail to load the image URL", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
                e -> Toast.makeText(this, "Fail to upload the image ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveUserProfileToDatabase(firstName: String, lastName: String, email: String, profileImage: String?) {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserID != null) {
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users").child(currentUserID)

            val userUpdates = hashMapOf<String, Any>(
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email
            )
            profileImage?.let { userUpdates["profileImage"] = it }

            databaseReference.updateChildren(userUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Profile Update Complete", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity after successful update
                } else {
                    Toast.makeText(this, "Profile Update Fail", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Cannot find the user information", Toast.LENGTH_SHORT).show()
        }
    }
}

