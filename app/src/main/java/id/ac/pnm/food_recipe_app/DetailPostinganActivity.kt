package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearEasing
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

// Activity ini dibuat Kevin sebagai placeholder
// Anam yang akan melengkapi konten detail postingan dan komentar
class DetailPostinganActivity : AppCompatActivity() {

    private lateinit var databaseFavoritRef: com.google.firebase.database.DatabaseReference
    private lateinit var firebaseAuth: com.google.firebase.auth.FirebaseAuth
    private var isFavorit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)

        // Ambil data postingan dari intent
        val postingan = intent.getSerializableExtra("Extra_Postingan") as? Postingan

        val btnBack       = findViewById<ImageButton>(R.id.btnBack)
        val btnFavorite   = findViewById<ImageButton>(R.id.btnFavorite)
        val btnShare      = findViewById<ImageButton>(R.id.btnShare)

        val imgHeader     = findViewById<ImageView>(R.id.imgHeader)
        val txtJudul      = findViewById<TextView>(R.id.txtJudul)
        val txtDeskripi   = findViewById<TextView>(R.id.txtDesc)
        val btnUsername   = findViewById<MaterialButton>(R.id.btnUsername)

        val rvBahan       = findViewById<RecyclerView>(R.id.rvBahan)
        val rvLangkah     = findViewById<RecyclerView>(R.id.rvLangkah)
        val rvKomentar    = findViewById<RecyclerView>(R.id.rvKomentar)

        val txtEmptyKomen = findViewById<TextView>(R.id.txtEmptyKomen)
        val editKomentar  = findViewById<EditText>(R.id.editKomentar)
        val btnKirimKomen = findViewById<ImageButton>(R.id.btnKirimKomen)
        val imgProfilUser = findViewById<ImageView>(R.id.imgProfilUser)

        if (postingan != null) {
            txtJudul.text = postingan.judulResep
            btnUsername.text = postingan.namaUser
            txtDeskripi.text = postingan.deskripsi


            val stringBahan = postingan.bahan
            val listBahan: List<String> = stringBahan.split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            val stringlangkah = postingan.langkah
            val listLangkah: List<String> = stringlangkah.split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            rvBahan.layoutManager = LinearLayoutManager(this)
            rvBahan.adapter = BahanLangkahAdapter(listBahan, isLangkah = false)

            rvLangkah.layoutManager = LinearLayoutManager(this)
            rvLangkah.adapter = BahanLangkahAdapter(listLangkah, isLangkah = true)
        }

        btnBack.setOnClickListener { finish() }

        btnUsername.setOnClickListener {
            if (postingan != null) {
                val intent = Intent(this, ProfileUserActivity::class.java)
                intent.putExtra("USER_ID_PEMILIK", postingan.userId)
                startActivity(intent)
            }
        }

        btnFavorite.setOnClickListener {
            Toast.makeText(this, "Resep Disimpan ke Favorit6", Toast.LENGTH_SHORT).show()
        }
    }
}