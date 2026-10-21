package nodomain.aditya1875more.stashly.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SavedItem::class], version = 10, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
