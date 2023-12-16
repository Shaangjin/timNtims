package seoultech.itm.timntims

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.storage.FirebaseStorage


@GlideModule
class MyAppGlideModule : AppGlideModule()

class ImageHandler {


    fun uploadImage(fileUri: Uri, onUploadComplete: (Boolean, String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("photos/${fileUri.lastPathSegment}")

        imageRef.putFile(fileUri)
            .addOnSuccessListener {
                Log.d("Upload", "Image Uploaded Successfully")
                onUploadComplete(true, "Image Uploaded Successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Upload", "Image Upload Failed", e)
                onUploadComplete(false, "Image Upload Failed")
            }
    }


}
