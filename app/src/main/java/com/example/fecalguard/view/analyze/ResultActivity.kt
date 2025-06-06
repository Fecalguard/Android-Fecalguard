package com.example.fecalguard.view.analyze

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fecalguard.MainActivity
import com.example.fecalguard.MainViewModel
import com.example.fecalguard.R
import com.example.fecalguard.data.local.database.Disease
import com.example.fecalguard.data.local.database.DiseaseImage
import com.example.fecalguard.data.local.database.Medicine
import com.example.fecalguard.data.remote.response.DiagnosisResponse
import com.example.fecalguard.databinding.ActivityResultBinding
import com.example.fecalguard.view.analyze.adapter.DiseaseDetailTextAdapter
import com.example.fecalguard.view.analyze.adapter.DiseaseImageAdapter
import com.example.fecalguard.view.analyze.adapter.DiseaseMedicineAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent.getParcelableExtra<DiagnosisResponse>("data")

        Glide.with(binding.ivPhoto.context)
            .load(data?.imageUrl)
            .error(R.drawable.ic_place_holder)
            .into(binding.ivPhoto)

        if(data!= null){
            setupData(data)
        }

        supportActionBar?.title = "Detail Diagnosa Penyakit"
    }

    private fun setupData(data: DiagnosisResponse?) {
        if(data?.predictedClass?.contains("Healthy", ignoreCase = true) == true){
            binding.textResult.text = "Ayam Anda Sehat!"
            binding.iconHealthyOrSick.setImageResource(R.drawable.healthy)
        } else {
            binding.iconHealthyOrSick.setImageResource(R.drawable.detected)
            binding.textResult.text = "Terdeteksi penyakit ${data?.predictedClass}"
        }

        binding.textConfidence.text = String.format("Confidence score: ${data?.confidence}")

        setDetails(data?.predictedClass)
    }

    private fun setDetails(predictedClass: String?) {
        var diseaseId = -1
        if (predictedClass != null) {
            if(predictedClass.contains("salmonella", ignoreCase = true)){
                diseaseId = 2
            } else if (predictedClass.contains("new", ignoreCase = true)){
                diseaseId = 3
            } else if (predictedClass.contains("coccidiosis", ignoreCase = true)){
                diseaseId = 1
            } else {
                binding.cardCult.visibility = View.GONE
            }
        }
        if(diseaseId != -1){
            mainViewModel.getDiseaseDetails(diseaseId)
            mainViewModel.getDiseaseImages(diseaseId)
            mainViewModel.getDiseaseMedicines(diseaseId)

            mainViewModel.diseaseDetails.observe(this){result ->
                setDiseaseDetails(result)
            }
            mainViewModel.diseaseImages.observe(this){result ->
                setDiseaseImage(result)
            }
            mainViewModel.diseaseMedicines.observe(this){result ->
                setDiseaseMedicine(result)
            }

        }
    }

    private fun setDiseaseMedicine(medicines: List<Medicine>){
        binding.cardMedicine.visibility = View.VISIBLE
        binding.rvMeds.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = DiseaseMedicineAdapter(medicines)
        binding.rvMeds.adapter = adapter

        adapter.setOnItemClickCallback(object : DiseaseMedicineAdapter.OnItemClickCallback {
            override fun onItemClicked(medicine: Medicine) {
                val medicineLink = medicine.medicineLink
                if (medicineLink.isNotEmpty()) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, medicineLink.toUri())
                    startActivity(browserIntent)
                } else {
                    Toast.makeText(this@ResultActivity, "Link tidak dapat diakses", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

    private fun setDiseaseImage(images: List<DiseaseImage>){
        var appear = false
        binding.hiddenLayoutImage.visibility = View.GONE

        binding.chevronImage.setOnClickListener {
            if(appear){
                appear = false
                binding.chevronImage.setImageResource(R.drawable.baseline_expand_more_24)
                binding.hiddenLayoutImage.visibility = View.GONE
            }else{
                appear = true
                binding.chevronImage.setImageResource(R.drawable.baseline_expand_less_24)
                binding.hiddenLayoutImage.visibility = View.VISIBLE
            }
        }

        binding.titleText.setOnClickListener {
            if(appear){
                appear = false
                binding.chevronImage.setImageResource(R.drawable.baseline_expand_more_24)
                binding.hiddenLayoutImage.visibility = View.GONE
            }else{
                appear = true
                binding.chevronImage.setImageResource(R.drawable.baseline_expand_less_24)
                binding.hiddenLayoutImage.visibility = View.VISIBLE
            }
        }

        binding.rvImage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = DiseaseImageAdapter(images)
        binding.rvImage.adapter = adapter
    }

    private fun setDiseaseDetails(data: Disease) {
        val diseaseHtmlList = mainViewModel.getAllHtmlSections(data)
        binding.rvCult.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = DiseaseDetailTextAdapter(diseaseHtmlList)
        binding.rvCult.adapter = adapter
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