package com.example.mobilevision

import android.graphics.Bitmap
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mobilevision.databinding.FragmentImageTextExtractionBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ImageTextExtractionFragment : Fragment() {

    private val vm : SharedViewModel by activityViewModels()

    companion object {
        private const val BITMAP_KEY = "bitmap"
        fun newInstance(bitmap: Bitmap): ImageTextExtractionFragment {
            return ImageTextExtractionFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BITMAP_KEY, bitmap)
                }
            }
        }
    }

    private lateinit var binding: FragmentImageTextExtractionBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageTextExtractionBinding.inflate(inflater, container, false)
        initBundle()
        return binding.root
    }

    private var bitmap: Bitmap? = null
    private fun initBundle() {
        arguments?.let {
            if (it.containsKey(BITMAP_KEY)) {
                bitmap = it.getParcelable(BITMAP_KEY)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        recognize()
    }

    private fun initViews() {
        binding.apply {
            iv.setImageBitmap(bitmap)
            iv.rotation = 90f
            tv.movementMethod = ScrollingMovementMethod()
            fab.visibility = View.GONE
            fab.setOnClickListener {
                openTextTranslationFragment()
            }
        }
    }

    private fun openTextTranslationFragment() {
        if(::textToTranslate.isInitialized) {
            vm.openTextTranslationFragment(textToTranslate)
        }
    }
    lateinit var textToTranslate: String
    private fun recognize() {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = bitmap?.let { InputImage.fromBitmap(it, 90) }

        if (image != null) {
            recognizer.process(image).addOnSuccessListener { visionText ->
                binding.tv.text = visionText.text
                textToTranslate = visionText.text
                binding.fab.visibility = View.VISIBLE
                recognizer.close()
            }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        it.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    recognizer.close()
                }
        }
    }
}