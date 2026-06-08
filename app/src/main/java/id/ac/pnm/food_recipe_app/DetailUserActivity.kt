package id.ac.pnm.food_recipe_app

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailUserActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnBookmark: ImageView
    private lateinit var btnShare: ImageView

    private lateinit var txtJudul: TextView
    private lateinit var txtUsername: TextView
    private lateinit var txtDeskripsi: TextView
    private lateinit var txtBahan: TextView
    private lateinit var txtLangkah: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)

        // Hubungkan View
        btnBack = findViewById(R.id.btnBack)
        btnBookmark = findViewById(R.id.btnBookmark)
        btnShare = findViewById(R.id.btnShare)

        txtJudul = findViewById(R.id.txtJudul)
        txtUsername = findViewById(R.id.btnUsername)
        txtDeskripsi = findViewById(R.id.txtDeskripsi)
        txtBahan = findViewById(R.id.txtBahan)
        txtLangkah = findViewById(R.id.txtLangkah)

        // Tombol kembali
        btnBack.setOnClickListener {
            finish()
        }

        // Tampilkan data postingan
        tampilkanDataPostingan()
    }

    private fun tampilkanDataPostingan() {

        val postinganId = intent.getIntExtra("POSTINGAN_ID", -1)

        val postingan = PostinganDataSource
            .getAllPostingan()
            .find { it.id == postinganId }

        if (postingan != null) {

            txtJudul.text = postingan.judulResep

            txtUsername.text = "@${postingan.namaUser}"

            txtDeskripsi.text = postingan.deskripsi

            //txtBahan.text = postingan.bahan

            //txtLangkah.text = postingan.langkah
        }
    }
}