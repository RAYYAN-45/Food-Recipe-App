package id.ac.pnm.food_recipe_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import id.ac.pnm.food_recipe_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        replaceFragment(Home_Fragment())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.Home -> replaceFragment(Home_Fragment())
                R.id.Profile -> replaceFragment(Profile_Fragment())
                R.id.Favorite -> replaceFragment(Fav_Fragment())

                else ->{



                }

            }

            true

        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun replaceFragment(fragment: Fragment){

        val framentManager = supportFragmentManager
        val fragmentTransaction = framentManager.beginTransaction()
        fragmentTransaction.replace(R.id.Frame_Layout,fragment)
        fragmentTransaction.commit()


    }
}