package seoultech.itm.timntims

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.MemoryFormat
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var bitmap: Bitmap? = null
        var module: Module? = null
        try {
            // creating bitmap from packaged into app android asset 'image.jpg',
            // app/src/main/assets/image.jpg
            bitmap = BitmapFactory.decodeStream(assets.open("image.jpg"))
            // loading serialized torchscript module from packaged into app android asset model.pt,
            // app/src/model/assets/model.pt
            module = LiteModuleLoader.load(assetFilePath(this, "model.pt"))
        } catch (e: IOException) {
            Log.e("PytorchHelloWorld", "Error reading assets", e)
            finish()
        }

        // showing image on UI
        val imageView = findViewById<ImageView>(R.id.image)
        imageView.setImageBitmap(bitmap)

        // preparing input tensor
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB,
            MemoryFormat.CHANNELS_LAST
        )

        // running the model
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

        // showing className on UI
        val textView = findViewById<TextView>(R.id.text)
        textView.text = className
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