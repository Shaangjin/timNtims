package seoultech.itm.timntims

import com.bumptech.glide.Glide
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.storage.FirebaseStorage

@GlideModule
class MyAppGlideModule : AppGlideModule()

class ImageHandler {


    fun uploadImage(fileUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("photos/${fileUri.lastPathSegment}")

        imageRef.putFile(fileUri)
            .addOnSuccessListener {
                Log.d("Upload", "Image Uploaded Successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Upload", "Image Upload Failed", e)
            }
    }

    fun downloadImage(imageView: ImageView) {
        val ref = FirebaseStorage.getInstance().reference.child("photos/dog.jpg")

        Glide.with(imageView.context)
            .load(ref)
            .centerCrop()
            .into(imageView)
    }
}
