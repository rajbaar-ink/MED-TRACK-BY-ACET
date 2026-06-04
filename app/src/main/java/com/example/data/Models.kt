package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dosage: String,
    val frequency: String, // "Daily", "Weekly", "As needed", etc.
    val timesString: String, // Comma-separated times (e.g., "08:00,20:00")
    val instructions: String, // "After meal", "Before meal", etc.
    val qtyRemaining: Int,
    val qtyNeeded: Int, // Low stock warning threshold
    val isNotificationEnabled: Boolean = true,
    val categoryColorValue: Int, // Color color code in Long/Int
    val type: String // "Pill", "Syrup", "Injection", "Inhaler", "Other"
) {
    val times: List<String>
        get() = if (timesString.isEmpty()) emptyList() else timesString.split(",").map { it.trim() }
}

@Entity(tableName = "medication_logs")
data class MedicationLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: Int,
    val medicineName: String,
    val dosage: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String, // "Taken", "Skipped", "Missed"
    val notes: String = ""
)

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val doctorName: String,
    val specialty: String,
    val appointmentTime: Long, // unix milliseconds
    val location: String,
    val notes: String,
    val status: String = "Scheduled" // "Scheduled", "Completed", "Cancelled"
)

@Entity(tableName = "medical_reports")
data class MedicalReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // "Prescription", "Lab Report", "Doctor Note", "Vaccine"
    val date: Long = System.currentTimeMillis(),
    val description: String,
    val filePath: String? = null, // Path to local storage or mocked upload URI
    val doctorAssigned: String? = null
)

@Entity(tableName = "caregivers_contacts")
data class CaregiverContact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String, // "Doctor", "Caregiver", "Emergency Contact"
    val contactNumber: String,
    val email: String,
    val notes: String = "",
    val isEmergencyContact: Boolean = false
)
