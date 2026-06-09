package id.ac.pnm.food_recipe_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TambahResepActivity : AppCompatActivity() {

    private lateinit var etNamaMenu: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var etBahan: EditText
    private lateinit var etLangkah: EditText
    private lateinit var btnBack: ImageButton
    private lateinit var btnSimpanPostingan: Button

    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_resep)


        etNamaMenu = findViewById(R.id.etNamaMenu)
        etDeskripsi = findViewById(R.id.etDeskripsi)
        etBahan = findViewById(R.id.etBahan)
        etLangkah = findViewById(R.id.etLangkah)
        btnBack = findViewById(R.id.btnBack)
        btnSimpanPostingan = findViewById(R.id.btnSimpanPostingan)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("postingan")

        btnBack.setOnClickListener {
            finish()
        }

        btnSimpanPostingan.setOnClickListener{
            simpanKefirebase()
            finish()
        }
    }

    private fun simpanKefirebase(){
        val namaMenu = etNamaMenu.text.toString().trim()
        val deskripsi = etDeskripsi.text.toString().trim()
        val bahan = etBahan.text.toString().trim()
        val langkah = etLangkah.text.toString().trim()


        if (namaMenu.isEmpty() || deskripsi.isEmpty() || bahan.isEmpty() || langkah.isEmpty()){
            Toast.makeText(this, "Semua Kolom Haru Diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser != null){
            val userId = currentUser.uid
            val namaUser = currentUser.displayName ?: "Chef Eurocuisine"
            val postId = databaseRef.push().key

            if (postId != null){
                val postinganBaru = Postingan(
                    postId = postId,
                    userId = userId,
                    namaUser = namaUser,
                    judulResep = namaMenu,
                    deskripsi = deskripsi,
                    bahan = bahan,
                    langkah = langkah,
                    timestamp = System.currentTimeMillis()
                )

                databaseRef.child(postId).setValue(postinganBaru)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Resep berhasil Diposting", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal Posting : ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(this, "Harus Login Sebelum Posting", Toast.LENGTH_SHORT).show()
            }

        }
    }
}