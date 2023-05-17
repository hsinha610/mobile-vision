package com.example.mobilevision

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import com.example.mobilevision.databinding.FragmentImageCaptureBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit

class ImageCaptureFragment : Fragment() {

    private lateinit var cameraExecutor: ExecutorService


    val viewModel : SharedViewModel by activityViewModels()

    private lateinit var binding: FragmentImageCaptureBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageCaptureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private var imageCapture: ImageCapture = buildImageCaptureUseCase()
    private fun init() {
        initCamera()
        initClickListeners()
        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun initCamera() {
        val cameraProviderFuture = context?.let { ProcessCameraProvider.getInstance(it) }

        context?.let { ctx -> ContextCompat.getMainExecutor(ctx) }?.let { executor ->
            cameraProviderFuture?.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build().also { preview ->
                        preview.setSurfaceProvider(binding.preview.surfaceProvider)
                    }

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA


                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture
                    )

                } catch (exc: Exception) {
                    Log.e("TAG", "Use case binding failed", exc)
                }

            }, executor)
        }
    }

    private fun initClickListeners() {
        binding.btn.setOnClickListener {

            binding.btn.isEnabled = false
            binding.loader.visibility = View.VISIBLE


            context?.let { ctx ->
                ContextCompat.getMainExecutor(
                    ctx
                )
            }?.let { it1 ->
                imageCapture.takePicture(it1, @ExperimentalGetImage object : ImageCapture.OnImageCapturedCallback() {

                    override fun onCaptureSuccess(image: ImageProxy) {
                        super.onCaptureSuccess(image)
                        val buffer = image.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        viewModel.openImageTextExtractionFragment(bitmap)
                        image.close()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        super.onError(exception)
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                        binding.btn.isEnabled = true
                        binding.loader.visibility = View.GONE
                    }
                })
            }
        }
    }

    private fun buildImageCaptureUseCase(): ImageCapture {
        return ImageCapture.Builder()
            .setFlashMode(FLASH_MODE_AUTO)
            .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }

    companion object {
        fun newInstance(): ImageCaptureFragment {
            return ImageCaptureFragment().apply {
                arguments = Bundle()
            }
        }
    }
}