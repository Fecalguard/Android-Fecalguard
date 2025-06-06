package com.example.fecalguard

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.fecalguard.data.local.UserSession
import com.example.fecalguard.data.local.database.DiseaseWithDetails
import com.example.fecalguard.data.local.database.Disease
import com.example.fecalguard.data.local.database.DiseaseDao
import com.example.fecalguard.data.local.database.DiseaseImage
import com.example.fecalguard.data.local.database.Medicine
import com.example.fecalguard.data.remote.response.DiagnosisResponse
import com.example.fecalguard.data.remote.response.LoginResponse
import com.example.fecalguard.data.remote.response.MessageResponse
import com.example.fecalguard.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val diseaseDao: DiseaseDao
) : ViewModel() {
    var currentImageUri: Uri? = null

    private val _registerResult = MutableLiveData<Result<MessageResponse>>()
    val registerResult: LiveData<Result<MessageResponse>> get() = _registerResult

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    private val _diagnosisResult = MutableLiveData<Result<DiagnosisResponse>>()
    val diagnosisResult: LiveData<Result<DiagnosisResponse>> get() = _diagnosisResult

    private val _historyResult = MutableLiveData<Result<List<DiagnosisResponse>>>()
    val historyResult: LiveData<Result<List<DiagnosisResponse>>> get() = _historyResult

    private val _emptyFieldError = MutableLiveData<String>()
    val emptyFieldError: LiveData<String> = _emptyFieldError

    private val _diseaseDetails = MutableLiveData<Disease>()
    val diseaseDetails: LiveData<Disease> = _diseaseDetails

    private val _diseaseImages = MutableLiveData<List<DiseaseImage>>()
    val diseaseImages: LiveData<List<DiseaseImage>> = _diseaseImages

    private val _diseaseMedicines = MutableLiveData<List<Medicine>>()
    val diseaseMedicines: LiveData<List<Medicine>> = _diseaseMedicines

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _emptyFieldError.value = "Alamat email dan kata sandi tidak boleh kosong!"
            return
        }

        viewModelScope.launch {
            mainRepository.register(email, password)
                .collect { result ->
                    _registerResult.value = result
                }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _emptyFieldError.value = "Alamat email dan kata sandi tidak boleh kosong!"
            return
        }

        viewModelScope.launch {
            mainRepository.login(email, password)
                .collect { result ->
                    _loginResult.value = result
                }
        }
    }

    fun getSession(): LiveData<UserSession> {
        return mainRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            mainRepository.logout()
        }
    }

    fun getDiagnosis(image: File) {
        viewModelScope.launch {
            mainRepository.getDiagnosis(image)
                .collect { result ->
                    _diagnosisResult.value = result
                }
        }
    }

    fun getHistory(){
        viewModelScope.launch {
            mainRepository.getAllHistory()
                .collect { result ->
                    _historyResult.value = result
                }
        }
    }

    fun getDiseaseDetails(diseaseId: Int) {
        viewModelScope.launch {
            val result = mainRepository.getDisease(diseaseId)
            _diseaseDetails.value = result
        }
    }

    fun getDiseaseImages(diseaseId: Int) {
        viewModelScope.launch {
            val result = mainRepository.getDiseaseImage(diseaseId)
            _diseaseImages.value = result
        }
    }

    fun getDiseaseMedicines(diseaseId: Int) {
        viewModelScope.launch {
            val result = mainRepository.getMedicine(diseaseId)
            _diseaseMedicines.value = result
        }
    }

    fun getAllHtmlSections(disease: Disease): List<String> {
        return listOf(
            disease.symptoms,
            disease.sideEffects,
            disease.quickTreatment,
            disease.longTermSolution
        )
    }

}