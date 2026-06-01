package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import id.ac.pnm.food_recipe_app.data.local.database.FoodDatabase
import id.ac.pnm.food_recipe_app.data.local.entity.FoodEntity
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Insert data resep ke Room kalau belum ada
        val db = FoodDatabase.getDatabase(this)
        lifecycleScope.launch {
            val count = db.foodDao().getCount()
            if (count == 0) {
                // Insert semua data resep
                val dataResep = listOf(
                    FoodEntity(
                        id = 1,
                        title = "Boeuf Bourguignon",
                        desc = "Hidangan khas Perancis dengan daging sapi dimasak anggur merah...",
                        image = R.drawable.item_1,
                        ingredients = listOf(
                            "1 kg daging sapi potong dadu",
                            "200 ml anggur merah",
                            "2 wortel potong dadu",
                            "1 bawang bombay",
                            "3 siung bawang putih",
                            "2 sdm tepung terigu",
                            "Garam dan merica",
                            "Thyme dan bay leaf"
                        ),
                        steps = listOf(
                            "Lumuri daging dengan tepung, garam, dan merica",
                            "Tumis bawang bombay dan bawang putih hingga harum",
                            "Masukkan daging, masak hingga berubah warna",
                            "Tuang anggur merah, aduk rata",
                            "Tambahkan wortel dan bumbu aromatik",
                            "Masak dengan api kecil selama 2-3 jam hingga daging empuk",
                            "Sajikan hangat"
                        )
                    ),
                    FoodEntity(
                        id = 2,
                        title = "Paella",
                        desc = "Hidangan khas Spanyol dengan nasi saffron dicampur makanan laut...",
                        image = R.drawable.paella,
                        ingredients = listOf(
                            "300 gr beras",
                            "200 gr udang",
                            "150 gr kerang",
                            "100 gr cumi",
                            "1 paprika merah",
                            "1 tomat",
                            "Saffron",
                            "Kaldu ayam 500 ml",
                            "Minyak zaitun"
                        ),
                        steps = listOf(
                            "Panaskan minyak zaitun dalam wajan paella",
                            "Tumis paprika dan tomat hingga layu",
                            "Masukkan beras, aduk hingga tercampur",
                            "Tuang kaldu ayam dan saffron",
                            "Masak tanpa diaduk selama 15 menit",
                            "Tata udang, kerang, dan cumi di atas nasi",
                            "Masak hingga seafood matang dan nasi pulen",
                            "Sajikan dengan lemon"
                        )
                    ),
                    FoodEntity(
                        id = 3,
                        title = "Souvlaki",
                        desc = "Hidangan Yunani berupa potongan daging panggang...",
                        image = R.drawable.souvlaki,
                        ingredients = listOf(
                            "500 gr daging ayam/domba potong dadu",
                            "3 sdm minyak zaitun",
                            "2 siung bawang putih cincang",
                            "1 sdt oregano kering",
                            "Jus 1 lemon",
                            "Garam dan merica",
                            "Roti pita",
                            "Tzatziki sauce"
                        ),
                        steps = listOf(
                            "Campur daging dengan minyak zaitun, bawang putih, oregano, lemon, garam, merica",
                            "Diamkan minimal 1 jam di kulkas",
                            "Tusuk daging ke tusuk sate",
                            "Panggang di atas api/grill hingga matang kecoklatan",
                            "Hangatkan roti pita",
                            "Sajikan souvlaki dengan roti pita dan tzatziki sauce"
                        )
                    )
                )
                dataResep.forEach { db.foodDao().insertFood(it) }
            }
        }

        // Delay splash screen 3 detik lalu ke Login
        Handler(Looper.getMainLooper()).postDelayed({
            goToLoginActivity()
        }, 3000L)
    }

    private fun goToLoginActivity() {
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}