package nodomain.aditya1875more.stashly.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import nodomain.aditya1875more.stashly.utils.ContentTypeConverter

@Database(entities = [SavedItem::class], version = 9, exportSchema = false)
@TypeConverters(ContentTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
