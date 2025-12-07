package id.ac.pnm.food_recipe_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username = intent.getStringExtra("Username")

        bottomNav = findViewById(R.id.bottomNavigationView)

        if (savedInstanceState == null) {
            loadFragment(Home_Fragment().apply {
                arguments = Bundle().apply {
                    putString("Username", username)
                }
            })
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.Home -> {
                    loadFragment(Home_Fragment())
                    true
                }

                R.id.Favorite -> {
                    loadFragment(FavoritFragment())
                    true
                }

                R.id.Profile -> {
                    loadFragment(Profile_Fragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.Frame_Layout, fragment)
            .commit()
    }
}
