package id.ac.pnm.food_recipe_app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.ac.pnm.food_recipe_app.data.local.Converters
import id.ac.pnm.food_recipe_app.data.local.dao.FoodDao
import id.ac.pnm.food_recipe_app.data.local.entity.FoodEntity

@Database(
    entities = [FoodEntity::class],
    version = 2
)

@TypeConverters(Converters::class)


abstract class FoodDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao

    companion object{

        @Volatile
        private var INSTANCE: FoodDatabase?= null

        fun getDatabase(context: Context): FoodDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FoodDatabase::class.java,
                    "food_database"
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance

                instance
            }
        }
    }
}