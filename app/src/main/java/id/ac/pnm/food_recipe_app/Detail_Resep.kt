package id.ac.pnm.food_recipe_app

import TextAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Detail_Resep : AppCompatActivity() {

    private lateinit var rvBahan: RecyclerView
    private lateinit var rvLangkah: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_resep)

        rvBahan = findViewById(R.id.rvBahan)
        rvLangkah = findViewById(R.id.rvLangkah)

        rvBahan.layoutManager = LinearLayoutManager(this)
        rvLangkah.layoutManager = LinearLayoutManager(this)

        val bahanList = listOf(
            "1 kg Daging Sapi (potong 4–5 cm)",
            "150 gr Bacon atau Pancetta",
            "1 botol (750 ml) Anggur Merah",
            "500 ml Kaldu Sapi",
            "2 sdm Pasta Tomat",
            "2 sdm Tepung Terigu",
            "3 sdm Mentega",
            "Minyak Zaitun secukupnya",
            "1 bawang bombay cincang",
            "2 wortel cincang",
            "2 batang seledri cincang",
            "4 siung bawang putih",
            "Garam & lada hitam"
        )

        val langkahList = listOf(
            "1. Keringkan daging menggunakan tisu dapur.",
            "2. Tumis bacon hingga minyak keluar.",
            "3. Masukkan daging, tumis hingga kecokelatan.",
            "4. Tumis bawang & sayuran.",
            "5. Masukkan pasta tomat, aduk rata.",
            "6. Taburkan tepung terigu, aduk cepat.",
            "7. Tuang anggur merah, masak hingga berkurang.",
            "8. Masukkan kaldu & bumbu aromatik.",
            "9. Masak 2–3 jam hingga empuk.",
            "10. Sajikan dengan bawang kecil, jamur, dan kentang."
        )

        rvBahan.adapter = TextAdapter(bahanList)
        rvLangkah.adapter = TextAdapter(langkahList)
    }
}
