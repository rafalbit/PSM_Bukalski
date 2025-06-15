package wat.psm_lab2_bukalski.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM person_table")
    suspend fun getAll(): List<Person>

    @Query("SELECT * FROM person_table WHERE name_column LIKE :name LIMIT 10")
    fun findByName(name: String): List<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(person: Person)

    @Query("DELETE FROM person_table")
    fun clearDB()

    @Query("SELECT COUNT(*) FROM person_table")
    fun getSize(): Int

    @Query("SELECT * FROM person_table WHERE name_column = :name")
    suspend fun getByNameExact(name: String): Person?

    @Query("SELECT * FROM person_table WHERE surname_column = :name")
    suspend fun getBySurnameExact(name: String): Person?

    @Query("SELECT * FROM person_table WHERE name_column = :name AND surname_column = :surname LIMIT 1")
    suspend fun getByFullName(name: String, surname: String): Person?

    @Query("DELETE FROM person_table WHERE id = :personId")
    suspend fun deletePerson(personId: Long)

    @Query("SELECT * FROM person_table")
    fun getAllPersons(): Flow<List<Person>>

    @Update
    suspend fun updatePerson(person: Person)

    @Query("""
    SELECT * FROM person_table
    WHERE 
        (name_column || ' ' || surname_column) LIKE '%' || :query || '%'
        OR name_column LIKE '%' || :query || '%'
        OR surname_column LIKE '%' || :query || '%'
""")
    suspend fun searchByNameOrSurname(query: String): List<Person>

}
