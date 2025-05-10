package wat.psm_lab2_bukalski

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "person_table")
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name_column") val name: String,
    @ColumnInfo(name = "surname_column") val surname: String,
)
