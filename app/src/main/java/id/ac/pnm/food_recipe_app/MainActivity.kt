package id.ac.pnm.food_recipe_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.ac.pnm.food_recipe_app.data.local.database.FoodDatabase
import id.ac.pnm.food_recipe_app.data.local.toEntity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username = intent.getStringExtra("Username")

        bottomNav = findViewById(R.id.bottomNavigationView)

        //buat home fragment jadi default
        if (savedInstanceState == null) {
            loadFragment(Home_Fragment().apply {
                arguments = Bundle().apply {
                    putString("Username", username)
                }
            })
        }

        setupBottomNavigation()
        seedDatabase()
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                //kondisi ketika home fragment
                R.id.Home -> {
                    loadFragment(Home_Fragment())
                    true
                }
                //Kondisi Ketika komun fragment
                R.id.Komunitas -> {
                    loadFragment(Komun_Fragment())
                    true
                }
                //kondisi ketika fevorite fragment
                R.id.Favorite -> {
                    loadFragment(FavoritFragment())
                    true
                }
                //kondisi ketika profile fragment
                R.id.Profile -> {
                    loadFragment(Profile_Fragment())
                    true
                }

                else -> false
            }
        }
    }

    //fungsi pinfaah fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.Frame_Layout, fragment)
            .commit()
    }

    //fungsi insert RoomDB
    private fun seedDatabase() {
        val db = FoodDatabase.getDatabase(this)
        lifecycleScope.launch {
            val dao = db.foodDao()

            if(dao.getCount() == 0){
                val foods = FoodDataSource
                    .getAllFoods()
                    .map { it.toEntity() }

                foods.forEach {
                    dao.insertFood(it)

                }
            }
        }
    }
}
