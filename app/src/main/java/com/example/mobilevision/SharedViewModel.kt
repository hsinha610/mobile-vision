package com.example.mobilevision

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val mld = MutableLiveData<Bitmap?>(null)
    val ld : LiveData<Bitmap?>
        get() = mld
    fun openImageTextExtractionFragment(bitmap: Bitmap) {
        mld.value = bitmap
    }

    private val mld2 = MutableLiveData<String?>(null)
    val ld2 : LiveData<String?>
        get() = mld2
    fun openTextTranslationFragment(text: String?) {
        mld2.value = text
    }
}