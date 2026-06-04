package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.ai.*
import com.example.ui.AlarmScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MedTrackViewModel(
    application: Application,
    private val repository: MedTrackRepository
) : AndroidViewModel(application) {

    // Authentication Services
    val supabaseAuthService = SupabaseAuthService()
    val firebaseAuthService = FirebaseAuthService(application)

    // Authentication Simulation State
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _isUserAuthenticated = MutableStateFlow(false)
    val isUserAuthenticated: StateFlow<Boolean> = _isUserAuthenticated.asStateFlow()

    private val _userName = MutableStateFlow<String>("User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _userToken = MutableStateFlow<String?>(null)
    val userToken: StateFlow<String?> = _userToken.asStateFlow()

    private val _activeAuthMethod = MutableStateFlow("Local") // "Local", "Supabase", "Firebase"
    val activeAuthMethod: StateFlow<String> = _activeAuthMethod.asStateFlow()

    // Loading and Error Middleware States
    private val _isDataLoading = MutableStateFlow(true)
    val isDataLoading: StateFlow<Boolean> = _isDataLoading.asStateFlow()

    private val _dbError = MutableStateFlow<String?>(null)
    val dbError: StateFlow<String?> = _dbError.asStateFlow()

    fun dismissDbError() {
        _dbError.value = null
    }

    // Error / Status Messages
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    // Room DB Observables (collected reactively by the UI)
    val medicines: StateFlow<List<Medicine>> = repository.allMedicines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<MedicationLog>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appointments: StateFlow<List<Appointment>> = repository.allAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reports: StateFlow<List<MedicalReport>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<CaregiverContact>> = repository.allContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Real-Time Active Medicine Alarm State (For UI full-screen overlay popup reminders)
    private val _activeAlarmMedicine = MutableStateFlow<Medicine?>(null)
    val activeAlarmMedicine: StateFlow<Medicine?> = _activeAlarmMedicine.asStateFlow()

    fun triggerActiveAlarm(medicine: Medicine) {
        _activeAlarmMedicine.value = medicine
    }

    fun dismissActiveAlarm() {
        _activeAlarmMedicine.value = null
    }

    // AI Chat Panel State
    private val _aiChatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            "Hello! I am your MedTrack AI and Symptom Checker Assistant. How can I help you today? \n\nDisclaimer: I provide educational health information and symptoms guidelines. I do not replace professional medical advice." to false
        )
    )
    val aiChatMessages: StateFlow<List<Pair<String, Boolean>>> = _aiChatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // OCR Scanner Simulation State
    private val _isOcrScanning = MutableStateFlow(false)
    val isOcrScanning: StateFlow<Boolean> = _isOcrScanning.asStateFlow()

    init {
        // Safe database seeding and initial loading
        viewModelScope.launch {
            try {
                _isDataLoading.value = true
                val initialList = repository.allMedicines.first()
                if (initialList.isEmpty()) {
                    seedDemoData()
                }
                // Simulate deep database synchronization, indexing, and loading
                kotlinx.coroutines.delay(650)
            } catch (e: Exception) {
                _dbError.value = "SQLite container initialization failed: ${e.localizedMessage}"
            } finally {
                _isDataLoading.value = false
            }
        }
    }

    private suspend fun seedDemoData() {
        // Add medicines
        repository.insertMedicine(
            Medicine(
                name = "Atorvastatin (Lipitor)",
                dosage = "20 mg",
                frequency = "Daily",
                timesString = "21:00",
                instructions = "Take at bedtime with water",
                qtyRemaining = 24,
                qtyNeeded = 5,
                categoryColorValue = 0xFF4CAF50.toInt(), // Green
                type = "Pill"
            )
        )
        repository.insertMedicine(
            Medicine(
                name = "Amoxicillin",
                dosage = "500 mg",
                frequency = "Daily",
                timesString = "08:00,14:00,20:00",
                instructions = "Take with or after food, finish full course",
                qtyRemaining = 12,
                qtyNeeded = 6,
                categoryColorValue = 0xFF2196F3.toInt(), // Blue
                type = "Pill"
            )
        )
        // Add appointment
        repository.insertAppointment(
            Appointment(
                doctorName = "Dr. Sarah Jenkins",
                specialty = "Cardiologist",
                appointmentTime = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000L), // 2 days from now
                location = "Heart Wellness Clinic, Suite 402",
                notes = "Fasting is required. Bring recent cholesterol panel reports."
            )
        )
        // Add medical report
        repository.insertReport(
            MedicalReport(
                title = "Annual Health Screening Report",
                type = "Lab Report",
                date = System.currentTimeMillis() - (15 * 24 * 60 * 60 * 1000L), // 15 days ago
                description = "General blood works show excellent liver function. LDL cholesterol slightly elevated (115 mg/dL). Recommended diet adjustments.",
                filePath = "mock_report_annual_screening_2026.pdf"
            )
        )
        // Add doctor contact
        repository.insertContact(
            CaregiverContact(
                name = "Dr. Sarah Jenkins",
                role = "Doctor",
                contactNumber = "+1 (555) 987-6543",
                email = "sarah.jenkins@hospital.org",
                notes = "Primary Cardiologist",
                isEmergencyContact = true
            )
        )
        // Add caregiver emergency contact
        repository.insertContact(
            CaregiverContact(
                name = "John Doe (Brother)",
                role = "Caregiver",
                contactNumber = "+1 (555) 123-4567",
                email = "johndoe@email.com",
                notes = "Primary emergency contact and caregiver coordinator",
                isEmergencyContact = true
            )
        )
    }

    // --- Authentication & Auth Error Handling Middleware ---
    private fun executeAuthAction(actionName: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                _isAuthLoading.value = true
                action()
            } catch (e: Exception) {
                val friendlyMsg = getFriendlyAuthErrorMessage(actionName, e)
                _dbError.value = friendlyMsg
                _toastMessage.emit(friendlyMsg)
            } finally {
                _isAuthLoading.value = false
            }
        }
    }

    private fun getFriendlyAuthErrorMessage(actionName: String, error: Exception): String {
        return when {
            error is java.net.UnknownHostException || error is java.net.ConnectException -> 
                "Remote security service is unreachable. Verify connection to perform $actionName."
            error is SecurityException -> 
                "Security clearance violation. Access denied during $actionName."
            else -> 
                "Authentication failure during $actionName: ${error.localizedMessage ?: "Check provider configuration or credentials."}"
        }
    }

    fun submitLogin(email: String, name: String) {
        executeAuthAction("Local Sandbox login") {
            if (email.isNotBlank()) {
                _activeAuthMethod.value = "Local"
                _userEmail.value = email
                _userName.value = if (name.isNotBlank()) name else "User"
                _isUserAuthenticated.value = true
                _userToken.value = "mock-jwt-token-12345"
                _toastMessage.emit("Welcome to MedTrack, ${_userName.value}!")
            } else {
                _toastMessage.emit("Please enter a valid email address.")
            }
        }
    }

    fun submitSupabaseLogin(email: String, secret: String) {
        executeAuthAction("Supabase authorization") {
            if (email.isBlank() || secret.isBlank()) {
                _toastMessage.emit("Please fill in both Email and Password fields.")
                return@executeAuthAction
            }
            _activeAuthMethod.value = "Supabase"
            
            val response = supabaseAuthService.signInWithEmail(email, secret)
            if (response.success) {
                _userEmail.value = response.email
                _userName.value = response.fullName ?: "Registered Patient"
                _userToken.value = response.accessToken
                _isUserAuthenticated.value = true
                _toastMessage.emit("Successfully Authenticated via Supabase!")
            } else {
                _toastMessage.emit(response.errorMessage ?: "Authentication failure.")
            }
        }
    }

    fun submitSupabaseSignUp(email: String, secret: String, fullName: String) {
        executeAuthAction("Supabase registration") {
            if (email.isBlank() || secret.isBlank() || fullName.isBlank()) {
                _toastMessage.emit("Please enter your Name, Email, and Password.")
                return@executeAuthAction
            }
            
            val response = supabaseAuthService.signUpWithEmail(email, secret, fullName)
            _toastMessage.emit(response.errorMessage ?: "Supabase signup request sent successfully.")
        }
    }

    fun submitFirebaseLogin(email: String, secret: String) {
        executeAuthAction("Firebase sign-in") {
            if (email.isBlank() || secret.isBlank()) {
                _toastMessage.emit("Please fill in both Email and Password fields.")
                return@executeAuthAction
            }
            _activeAuthMethod.value = "Firebase"
            
            val response = firebaseAuthService.signInWithFirebase(email, secret)
            if (response.success) {
                _userEmail.value = response.email
                _userName.value = if (response.fullName.isNullOrBlank()) "Firebase User" else response.fullName
                _userToken.value = response.accessToken
                _isUserAuthenticated.value = true
                _toastMessage.emit("Successfully Authenticated via Firebase Console!")
            } else {
                _toastMessage.emit(response.errorMessage ?: "Authorization failed on Firebase project 'acet-medtrack'.")
            }
        }
    }

    fun submitFirebaseSignUp(email: String, secret: String) {
        executeAuthAction("Firebase registration") {
            if (email.isBlank() || secret.isBlank()) {
                _toastMessage.emit("Please enter both Email and Password.")
                return@executeAuthAction
            }
            
            val response = firebaseAuthService.signUpWithFirebase(email, secret)
            if (response.success) {
                _toastMessage.emit("Firebase client account registered successfully! You can login now.")
            } else {
                _toastMessage.emit(response.errorMessage ?: "Failed to sign up on Firebase project 'acet-medtrack'.")
            }
        }
    }

    fun logout() {
        if (_activeAuthMethod.value == "Firebase") {
            firebaseAuthService.signOut()
        }
        _userEmail.value = null
        _userName.value = "Guest"
        _userToken.value = null
        _isUserAuthenticated.value = false
        _activeAuthMethod.value = "Local"
    }

    // --- Authentication & DB Error Handling Middleware ---
    private fun executeDbAction(actionName: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
            } catch (e: Exception) {
                val friendlyMsg = getFriendlyErrorMessage(actionName, e)
                _dbError.value = friendlyMsg
                _toastMessage.emit(friendlyMsg)
            }
        }
    }

    private fun getFriendlyErrorMessage(actionName: String, error: Exception): String {
        return when {
            error is java.io.IOException -> "Connectivity issues during $actionName. Remote syncing is momentarily disrupted."
            error is android.database.sqlite.SQLiteException -> "Local disk/database capacity issue encountered during $actionName. (Storage limits reached or database locked)"
            error is SecurityException -> "Secure access restriction. Permission denied during $actionName."
            else -> "Action failure ($actionName): ${error.localizedMessage ?: "Unknown medical database integrity error."}"
        }
    }

    // --- Medicines Actions ---
    fun addMedicine(
        name: String,
        dosage: String,
        frequency: String,
        times: List<String>,
        instructions: String,
        qtyRemaining: Int,
        qtyNeeded: Int,
        categoryColorValue: Int,
        type: String
    ) {
        executeDbAction("adding medicine") {
            val timesStr = times.joinToString(",")
            val medicine = Medicine(
                name = name,
                dosage = dosage,
                frequency = frequency,
                timesString = timesStr,
                instructions = instructions,
                qtyRemaining = qtyRemaining,
                qtyNeeded = qtyNeeded,
                categoryColorValue = categoryColorValue,
                type = type
            )
            val newId = repository.insertMedicine(medicine)
            val savedMed = medicine.copy(id = newId.toInt())
            AlarmScheduler.scheduleAlarmsForMedicine(getApplication(), savedMed)
            _toastMessage.emit("Medicine added successfully")
        }
    }

    fun updateMedicineDetails(medicine: Medicine) {
        executeDbAction("updating medicine details") {
            repository.updateMedicine(medicine)
            AlarmScheduler.scheduleAlarmsForMedicine(getApplication(), medicine)
            _toastMessage.emit("Medicine updated successfully")
        }
    }

    fun deleteMedicine(id: Int) {
        executeDbAction("removing medicine") {
            val medToDelete = repository.getMedicineById(id)
            if (medToDelete != null) {
                AlarmScheduler.cancelAlarmsForMedicine(getApplication(), medToDelete)
            }
            repository.deleteMedicine(id)
            _toastMessage.emit("Medicine removed")
        }
    }

    fun refillMedicine(id: Int, quantity: Int) {
        executeDbAction("refilling medicine") {
            val med = repository.getMedicineById(id)
            if (med != null) {
                val updated = med.copy(qtyRemaining = med.qtyRemaining + quantity)
                repository.updateMedicine(updated)
                _toastMessage.emit("Refilled ${med.name} with +$quantity doses")
            }
        }
    }

    // --- Medication Logging ---
    fun logMedicationProgress(medicine: Medicine, isTaken: Boolean, note: String = "") {
        executeDbAction("logging medication intake") {
            val status = if (isTaken) "Taken" else "Skipped"

            // Decrement quantity if taken
            if (isTaken && medicine.qtyRemaining > 0) {
                val updatedMed = medicine.copy(qtyRemaining = medicine.qtyRemaining - 1)
                repository.updateMedicine(updatedMed)
                if (updatedMed.qtyRemaining <= updatedMed.qtyNeeded) {
                    _toastMessage.emit("Warning: Low stock for ${medicine.name}! Only ${updatedMed.qtyRemaining} left.")
                }
            }

            // Record Log
            val log = MedicationLog(
                medicineId = medicine.id,
                medicineName = medicine.name,
                dosage = medicine.dosage,
                status = status,
                notes = note
            )
            repository.insertLog(log)
            _toastMessage.emit("Marked ${medicine.name} as $status")
        }
    }

    // --- Appointments ---
    fun addAppointment(doctorName: String, specialty: String, date: Long, location: String, notes: String) {
        executeDbAction("scheduling appointment") {
            val appt = Appointment(
                doctorName = doctorName,
                specialty = specialty,
                appointmentTime = date,
                location = location,
                notes = notes
            )
            repository.insertAppointment(appt)
            _toastMessage.emit("Appointment scheduled with $doctorName")
        }
    }

    fun updateAppointmentStatus(appointment: Appointment, status: String) {
        executeDbAction("updating appointment status") {
            repository.updateAppointment(appointment.copy(status = status))
            _toastMessage.emit("Appointment status updated")
        }
    }

    fun deleteAppointment(id: Int) {
        executeDbAction("cancelling appointment") {
            repository.deleteAppointment(id)
            _toastMessage.emit("Appointment cancelled")
        }
    }

    // --- Medical Reports / File Uploads ---
    fun uploadMedicalReport(title: String, type: String, description: String, mockFileName: String) {
        executeDbAction("uploading medical report") {
            val report = MedicalReport(
                title = title,
                type = type,
                description = description,
                filePath = mockFileName
            )
            repository.insertReport(report)
            _toastMessage.emit("Medical report uploaded successfully to database!")
        }
    }

    fun deleteReport(id: Int) {
        executeDbAction("deleting medical report") {
            repository.deleteReport(id)
            _toastMessage.emit("Report successfully deleted")
        }
    }

    // --- Caregivers / Doctor Contacts ---
    fun addContact(name: String, role: String, contactNumber: String, email: String, notes: String, isEmergency: Boolean) {
        executeDbAction("saving contact details") {
            val contact = CaregiverContact(
                name = name,
                role = role,
                contactNumber = contactNumber,
                email = email,
                notes = notes,
                isEmergencyContact = isEmergency
            )
            repository.insertContact(contact)
            _toastMessage.emit("Contact $name added")
        }
    }

    fun deleteContact(id: Int) {
        executeDbAction("deleting contact") {
            repository.deleteContact(id)
            _toastMessage.emit("Contact deleted")
        }
    }

    // --- Gemini AI Assistant Chat ---
    fun sendChatMessage(message: String) {
        val currentChat = _aiChatMessages.value.toMutableList()
        currentChat.add(message to true)
        _aiChatMessages.value = currentChat

        _isAiLoading.value = true

        viewModelScope.launch {
            try {
                // We pass in context about their current medications so the AI is smart and customized to them!
                val medList = medicines.value
                val contextPrompt = if (medList.isNotEmpty()) {
                    val medSummary = medList.joinToString("\n") { "- ${it.name} (${it.dosage}), frequency: ${it.frequency}, instructions: ${it.instructions}" }
                    "Context (The user is taking these medications):\n$medSummary\n\nUser Question: $message"
                } else {
                    message
                }

                val aiResponse = GeminiAssistant.getHealthAssistantResponse(
                    userPrompt = contextPrompt,
                    chatHistory = currentChat.dropLast(1)
                )

                _aiChatMessages.value = _aiChatMessages.value.toMutableList().apply {
                    add(aiResponse to false)
                }
            } catch (e: Exception) {
                _aiChatMessages.value = _aiChatMessages.value.toMutableList().apply {
                    add("Sorry, I had trouble connecting to the medical search network. Please try again." to false)
                }
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun clearChat() {
        _aiChatMessages.value = listOf(
            "Hello! Chat cleared. How can I help you regarding your medicines or wellness logs?" to false
        )
    }

    // --- OCR Prescription Scan Simulation ---
    fun executeOcrScan(rawOcrText: String, onScanCompleted: (PrescriptionScanResult) -> Unit) {
        _isOcrScanning.value = true
        viewModelScope.launch {
            val result = GeminiAssistant.extractPrescriptionDetails(rawOcrText)
            _isOcrScanning.value = false
            if (result.success) {
                _toastMessage.emit("AI extracted medicine: ${result.medicineName}!")
            } else {
                _toastMessage.emit(result.message)
            }
            onScanCompleted(result)
        }
    }

    // Factory Class for robust ViewModel creation
    class Factory(
        private val application: Application,
        private val repository: MedTrackRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MedTrackViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MedTrackViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
