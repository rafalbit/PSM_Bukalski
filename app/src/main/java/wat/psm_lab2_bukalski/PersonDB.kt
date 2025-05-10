package wat.psm_lab2_bukalski

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Person::class], version = 2, exportSchema = false)
abstract class PersonDB : RoomDatabase() {
    abstract fun personDao(): PersonDao

    companion object {
        private var INSTANCE: PersonDB? = null

        fun getPersonDB(context: Context): PersonDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE =
                        Room.databaseBuilder(
                            context.applicationContext,
                            PersonDB::class.java,
                            "personDB"
                        ).fallbackToDestructiveMigration() // ← To spowoduje, że Room usunie starą bazę i utworzy nową
                            .build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
