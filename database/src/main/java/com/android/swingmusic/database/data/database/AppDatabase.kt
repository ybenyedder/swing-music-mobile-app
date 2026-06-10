package com.android.swingmusic.database.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.swingmusic.database.data.converter.Converters
import com.android.swingmusic.database.data.dao.AccountDao
import com.android.swingmusic.database.data.dao.BaseUrlDao
import com.android.swingmusic.database.data.dao.LastPlayedTrackDao
import com.android.swingmusic.database.data.dao.QueueDao
import com.android.swingmusic.database.data.dao.UserDao
import com.android.swingmusic.database.data.entity.AccountEntity
import com.android.swingmusic.database.data.entity.BaseUrlEntity
import com.android.swingmusic.database.data.entity.LastPlayedTrackEntity
import com.android.swingmusic.database.data.entity.QueueEntity
import com.android.swingmusic.database.data.entity.UserEntity

@Database(
    entities = [
        QueueEntity::class,
        LastPlayedTrackEntity::class,
        BaseUrlEntity::class,
        UserEntity::class,
        AccountEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun baseUrlDao(): BaseUrlDao

    abstract fun queueDao(): QueueDao

    abstract fun lastPlayedTrackDao(): LastPlayedTrackDao

    abstract fun accountDao(): AccountDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `accounts` (
                        `accountKey` TEXT NOT NULL,
                        `userId` INTEGER NOT NULL,
                        `serverUrl` TEXT NOT NULL,
                        `username` TEXT NOT NULL,
                        `firstname` TEXT NOT NULL,
                        `lastname` TEXT NOT NULL,
                        `email` TEXT NOT NULL,
                        `image` TEXT NOT NULL,
                        `roles` TEXT NOT NULL,
                        `accessToken` TEXT NOT NULL,
                        `refreshToken` TEXT NOT NULL,
                        `maxAge` INTEGER NOT NULL,
                        `isActive` INTEGER NOT NULL,
                        PRIMARY KEY(`accountKey`)
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_queue_trackHash` ON `queue` (`trackHash`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_queue_albumHash` ON `queue` (`albumHash`)")
            }
        }
    }
}
