package id.ac.pnm.food_recipe_app

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

// Activity ini dibuat Kevin sebagai placeholder
// Anam yang akan melengkapi konten detail postingan dan komentar
class DetailPostinganActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_postingan)

        // Ambil data postingan dari intent
        val postingan = intent.getSerializableExtra("Extra_Postingan") as? Postingan

        val btnBack       = findViewById<ImageButton>(R.id.btnBackDetail)
        val txtNamaUser   = findViewById<TextView>(R.id.txtNamaUserDetail)
        val txtJudul      = findViewById<TextView>(R.id.txtJudulDetail)
        val txtDeskripsi  = findViewById<TextView>(R.id.txtDeskripsiDetail)

        if (postingan != null) {
            txtNamaUser.text  = postingan.namaUser
            txtJudul.text     = postingan.judulResep
            txtDeskripsi.text = postingan.deskripsi
        }

        btnBack.setOnClickListener { finish() }

        // ============================================
        // ANAM: tambahkan komentar dan fitur lain di sini
        // ============================================
    }
}