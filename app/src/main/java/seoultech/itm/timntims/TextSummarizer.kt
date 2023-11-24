package seoultech.itm.timntims

import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.tools.sentdetect.SentenceModel
import android.content.Context
import java.io.IOException

class TextSummarizer(private val context: Context) {

    private lateinit var sentenceModel: SentenceModel

    init {
        try {
            context.assets.open("en-sent.bin").use { modelIn ->
                sentenceModel = SentenceModel(modelIn)
            }
        } catch (e: IOException) {
            // Handle exception
        }
    }

    fun summarize(text: String, sentenceCount: Int): String {
        val sentenceDetector = SentenceDetectorME(sentenceModel)
        val sentences = sentenceDetector.sentDetect(text)
        return sentences.take(sentenceCount).joinToString(" ")
    }
}
