package seoultech.itm.timntims

import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.SimpleTokenizer
import android.content.Context
import java.io.IOException

class OrganizationNameFinder(private val context: Context) {

    private lateinit var nameFinderModel: TokenNameFinderModel

    init {
        try {
            // Load the organization name finder model
            context.assets.open("en-ner-organization.bin").use { modelIn ->
                nameFinderModel = TokenNameFinderModel(modelIn)
            }
        } catch (e: IOException) {
            // Handle exception if model file is not found or cannot be loaded
            e.printStackTrace() // This will print the stack trace to logcat
        }
    }

    fun findOrganizationNames(text: String): Array<String> {
        // Use OpenNLP's SimpleTokenizer to tokenize the text
        val tokenizer = SimpleTokenizer.INSTANCE
        val tokens = tokenizer.tokenize(text)

        // Create a NameFinder using the loaded model
        val nameFinder = NameFinderME(nameFinderModel)

        // Find names within the tokenized text
        val nameSpans = nameFinder.find(tokens)

        // Extract and return the organization names found in the text
        return nameSpans.map { span -> tokens.slice(span.getStart()..span.getEnd() - 1).joinToString(" ") }.toTypedArray()
    }
}
