package wat.psm_lab2_bukalski

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM person_table")
    suspend fun getAll(): List<Person>

    @Query("SELECT * FROM person_table WHERE name_column LIKE :name LIMIT 10")
    fun findByName(name: String): List<Person>

    @Query("SELECT * FROM person_table WHERE age_column > :age")
    fun findByAge(age: Int): List<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(person: Person)

    @Query("DELETE FROM person_table")
    fun clearDB()

    @Query("SELECT COUNT(*) FROM person_table")
    fun getSize(): Int
    // do logowania się w bazie danych
    @Query("SELECT * FROM person_table WHERE name_column = :name")
    suspend fun getByNameExact(name: String): Person?

}
