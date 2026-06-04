package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines ORDER BY id DESC")
    fun getAllMedicines(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE id = :id LIMIT 1")
    suspend fun getMedicineById(id: Int): Medicine?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine): Long

    @Update
    suspend fun updateMedicine(medicine: Medicine)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Int)
}

@Dao
interface MedicationLogDao {
    @Query("SELECT * FROM medication_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<MedicationLog>>

    @Query("SELECT * FROM medication_logs WHERE medicineId = :medicineId ORDER BY timestamp DESC")
    fun getLogsForMedicine(medicineId: Int): Flow<List<MedicationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MedicationLog)

    @Query("DELETE FROM medication_logs WHERE id = :id")
    suspend fun deleteLogById(id: Int)
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY appointmentTime ASC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment)

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointmentById(id: Int)
}

@Dao
interface MedicalReportDao {
    @Query("SELECT * FROM medical_reports ORDER BY date DESC")
    fun getAllReports(): Flow<List<MedicalReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: MedicalReport)

    @Query("DELETE FROM medical_reports WHERE id = :id")
    suspend fun deleteReportById(id: Int)
}

@Dao
interface CaregiverContactDao {
    @Query("SELECT * FROM caregivers_contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<CaregiverContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: CaregiverContact)

    @Update
    suspend fun updateContact(contact: CaregiverContact)

    @Query("DELETE FROM caregivers_contacts WHERE id = :id")
    suspend fun deleteContactById(id: Int)
}

@Database(
    entities = [
        Medicine::class,
        MedicationLog::class,
        Appointment::class,
        MedicalReport::class,
        CaregiverContact::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun medicationLogDao(): MedicationLogDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun medicalReportDao(): MedicalReportDao
    abstract fun caregiverContactDao(): CaregiverContactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medtrack_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
