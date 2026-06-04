package com.example.data

import kotlinx.coroutines.flow.Flow

class MedTrackRepository(private val database: AppDatabase) {

    // Medicines
    val allMedicines: Flow<List<Medicine>> = database.medicineDao().getAllMedicines()

    suspend fun getMedicineById(id: Int): Medicine? {
        return database.medicineDao().getMedicineById(id)
    }

    suspend fun insertMedicine(medicine: Medicine): Long {
        return database.medicineDao().insertMedicine(medicine)
    }

    suspend fun updateMedicine(medicine: Medicine) {
        database.medicineDao().updateMedicine(medicine)
    }

    suspend fun deleteMedicine(id: Int) {
        database.medicineDao().deleteMedicineById(id)
    }

    // Medication Logs
    val allLogs: Flow<List<MedicationLog>> = database.medicationLogDao().getAllLogs()

    fun getLogsForMedicine(medicineId: Int): Flow<List<MedicationLog>> {
        return database.medicationLogDao().getLogsForMedicine(medicineId)
    }

    suspend fun insertLog(log: MedicationLog) {
        database.medicationLogDao().insertLog(log)
    }

    suspend fun deleteLog(id: Int) {
        database.medicationLogDao().deleteLogById(id)
    }

    // Appointments
    val allAppointments: Flow<List<Appointment>> = database.appointmentDao().getAllAppointments()

    suspend fun insertAppointment(appointment: Appointment) {
        database.appointmentDao().insertAppointment(appointment)
    }

    suspend fun updateAppointment(appointment: Appointment) {
        database.appointmentDao().updateAppointment(appointment)
    }

    suspend fun deleteAppointment(id: Int) {
        database.appointmentDao().deleteAppointmentById(id)
    }

    // Medical Reports
    val allReports: Flow<List<MedicalReport>> = database.medicalReportDao().getAllReports()

    suspend fun insertReport(report: MedicalReport) {
        database.medicalReportDao().insertReport(report)
    }

    suspend fun deleteReport(id: Int) {
        database.medicalReportDao().deleteReportById(id)
    }

    // Contacts / Caregivers / Doctors
    val allContacts: Flow<List<CaregiverContact>> = database.caregiverContactDao().getAllContacts()

    suspend fun insertContact(contact: CaregiverContact) {
        database.caregiverContactDao().insertContact(contact)
    }

    suspend fun updateContact(contact: CaregiverContact) {
        database.caregiverContactDao().updateContact(contact)
    }

    suspend fun deleteContact(id: Int) {
        database.caregiverContactDao().deleteContactById(id)
    }
}
