package com.example.mobilevision

import android.os.Bundle
import android.provider.Contacts.SettingsColumns.KEY
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mobilevision.databinding.FragmentTextTranslationBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.text.Text

class TextTranslationFragment : Fragment() {

    private lateinit var binding: FragmentTextTranslationBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTextTranslationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initBundle()
        initViews()
        initTranslateText()
    }


    private lateinit var textToTranslate: String
    private fun initBundle() {
        arguments?.let {
            textToTranslate = it.getString(TEXT_KEY, "")
        }
    }

    private fun initViews() {
        binding.translatedEt.isEnabled = false
        binding.originalEt.isEnabled = false
        binding.originalEt.setText(textToTranslate)
        binding.progressCircular.visibility = View.VISIBLE
    }

    private lateinit var translator: Translator
    private fun initTranslateText() {
        val options = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.HINDI).build()
         translator = Translation.getClient(options)
        downloadModel()

    }

    private fun downloadModel () {
        Toast.makeText(context,"Downloading en-hi language model", Toast.LENGTH_SHORT).show()
        translator.downloadModelIfNeeded(DownloadConditions.Builder().build()).addOnSuccessListener {
            translateText()
        }.addOnFailureListener{
            Toast.makeText(context,"Not able to download language model", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStackImmediate()
        }
    }

    private fun translateText() {
        translator.translate(textToTranslate).addOnSuccessListener {
            binding.translatedEt.setText(it.toString())
            enableEditTexts()

        }.addOnFailureListener {
            binding.translatedEt.setText(it.toString())
            enableEditTexts()
        }
    }

    override fun onDestroyView() {
        translator.close()
        super.onDestroyView()

    }


    private fun enableEditTexts() {
        binding.translatedEt.isEnabled = true
        binding.originalEt.isEnabled = true
        binding.progressCircular.visibility = View.GONE
    }


    companion object {
        const val TEXT_KEY = "text_key"
        fun newInstance(textToTranslate: String): TextTranslationFragment {
            val fragment = TextTranslationFragment()
            val bundle = Bundle()
            bundle.putString(TEXT_KEY, textToTranslate)
            fragment.arguments = bundle
            return fragment
        }
    }
}