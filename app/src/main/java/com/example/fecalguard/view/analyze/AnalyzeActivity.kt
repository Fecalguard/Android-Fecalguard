package com.example.fecalguard.view.analyze

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.fecalguard.MainActivity
import com.example.fecalguard.MainViewModel
import com.example.fecalguard.data.remote.response.DiagnosisResponse
import com.example.fecalguard.databinding.ActivityAnalyzeBinding
import com.example.fecalguard.utils.Result
import com.example.fecalguard.utils.getImageUri
import com.example.fecalguard.utils.reduceFileImage
import com.example.fecalguard.utils.uriToFile
import com.example.fecalguard.view.dialog.FailedDialog
import com.example.fecalguard.view.dialog.LoadingDialog
import com.example.fecalguard.view.dialog.SuccessDialog
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class AnalyzeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalyzeBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val successDialog by lazy { SuccessDialog(this) }
    private val failedDialog by lazy { FailedDialog(this) }
    private val loadingDialog by lazy { LoadingDialog(this) }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyzeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Analisis Penyakit Ayam"

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        setupAction()

        mainViewModel.currentImageUri?.let {
            showImage()
        }
    }

    private fun setupAction() {
        binding.cardGallery.setOnClickListener { startGallery() }
        binding.cardCamera.setOnClickListener { startCamera() }

        binding.buttonAnalyze.setOnClickListener {
            mainViewModel.currentImageUri?.let {
                analyze(it)
            } ?: run {
                showToast("Tolong pilih gambar terlebih dahulu")
            }
        }
    }

    private fun analyze(it: Uri) {
        val image = uriToFile(it, this@AnalyzeActivity).reduceFileImage()
        mainViewModel.getDiagnosis(image)
        mainViewModel.diagnosisResult.observe(this){result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.startLoadingDialog()
                }
                is Result.Success -> {
                    loadingDialog.dismissDialog()
                    successDialog.startSuccessDialog("Berhasil Mendeteksi Penyakit!")
                    navigateToResult(result.data, it)
                }
                is Result.Error -> {
                    loadingDialog.dismissDialog()
                    failedDialog.startFailedDialog("Gagal Mendapatkan Analisis!")
                    Log.e("Upload", "Error processing profile update: ${result.message}")
                }
            }
        }
    }

    private fun startCamera() {
        mainViewModel.currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(mainViewModel.currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSaved: Boolean ->
        if (isSaved) {
            mainViewModel.currentImageUri?.let { uri ->
                startCrop(uri)
                showImage()
            }
        } else {
            Log.d("Photo Picker", "Photo was not saved")
        }
    }

    private fun navigateToResult(data: DiagnosisResponse, it: Uri) {
        lifecycleScope.launch {
            delay(2000)
            successDialog.dismissDialog()

            val intent = Intent(this@AnalyzeActivity, ResultActivity::class.java).apply {
                putExtra("data", data)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun startCrop(uri: Uri) {
        val croppedImg = createImageFile()
        if (croppedImg != null) {
            UCrop.of(uri, Uri.fromFile(croppedImg))
                .withAspectRatio(1f, 1f)
                .start(this)
        } else {
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_LONG).show()
        }
    }

    private fun createImageFile(): File? {
        val imageFileName = "${System.currentTimeMillis()}.jpg"
        return try {
            File(cacheDir, imageFileName).apply {
                createNewFile()
            }
        } catch (e: IOException) {
            Log.e("ImageError", "Error creating image file", e)
            null
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            mainViewModel.currentImageUri = uri
            startCrop(uri)
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val croppedImg = data?.let { UCrop.getOutput(it) }
            mainViewModel.currentImageUri = croppedImg
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val error = data?.let { UCrop.getError(it) }
            error?.let {
                showToast("Error: ${it.message}")
            }
        }
    }

    private fun showImage() {
        mainViewModel.currentImageUri?.let {
            binding.ivPhoto.setImageURI(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}