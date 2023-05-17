package com.example.mobilevision

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.mobilevision.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    val viewModel : SharedViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkForPermissions()
        init()
    }

    private fun init() {
        initViewModelObservers()
    }

    private fun initViewModelObservers() {
        viewModel.ld.observe(this){
            it?.let {
                openImageTextExtractionFragment(it)
            }
        }

        viewModel.ld2.observe(this){
            it?.let {
                openTextTranslationFragment(it)
            }
        }
    }

    private fun checkForPermissions() {
        if (allPermissionsGranted()) {
            supportFragmentManager.beginTransaction().apply {
                replace(binding.fragmentContainerView.id, ImageCaptureFragment.newInstance())
                addToBackStack("")
                commit()
            }
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }
    // 1st
    private fun openImageCaptureFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.fragmentContainerView.id, ImageCaptureFragment.newInstance())
            addToBackStack("1")
            commit()
        }
    }
    // 2nd
    private fun openImageTextExtractionFragment(bitmap: Bitmap) {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.fragmentContainerView.id, ImageTextExtractionFragment.newInstance(bitmap))
            addToBackStack("2")
            commit()
        }
    }
    // 3rd
    private fun openTextTranslationFragment(text: String) {
        val frag = supportFragmentManager.findFragmentByTag("2")
        supportFragmentManager.beginTransaction().apply {
            replace(binding.fragmentContainerView.id, TextTranslationFragment.newInstance(text))
            frag?.let{
                remove(it)
            }
            addToBackStack("3")
            commit()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object{
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).toTypedArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                openImageCaptureFragment()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}