package id.ac.pnm.food_recipe_app

import TextAdapter
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import id.ac.pnm.food_recipe_app.data.local.database.FoodDatabase
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Detail_Resep : AppCompatActivity() {

    // ===== KODE LAMA =====
    private lateinit var imgHeader: ImageView
    private lateinit var txtJudul: TextView
    private lateinit var rvBahan: RecyclerView
    private lateinit var rvLangkah: RecyclerView
    private lateinit var btnBack: View
    private lateinit var btnShare: View
    private lateinit var btnFavorite: ImageView
    private lateinit var layouContent: View

    // ===== KOMENTAR =====
    private lateinit var rvKomentar: RecyclerView
    private lateinit var editKomentar: EditText
    private lateinit var btnKirimKomen: ImageButton
    private lateinit var txtEmptyKomen: TextView
    private lateinit var komentarAdapter: KomentarAdapter
    private var foodId: Int = -1
    private lateinit var dbKomentarRef : com.google.firebase.database.DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_resep)

        // ===== Hubungkan id layout =====
        imgHeader    = findViewById(R.id.imgHeader)
        txtJudul     = findViewById(R.id.txtJudul)
        rvBahan      = findViewById(R.id.rvBahan)
        rvLangkah    = findViewById(R.id.rvLangkah)
        btnBack      = findViewById(R.id.btnBack)
        btnShare     = findViewById(R.id.btnShare)
        btnFavorite  = findViewById(R.id.btnFavorite)
        layouContent = findViewById(R.id.layoutContent)

        // Komentar
        rvKomentar = findViewById(R.id.rvKomentar)
        editKomentar = findViewById(R.id.editKomentar)
        btnKirimKomen = findViewById(R.id.btnKirimKomen)
        txtEmptyKomen = findViewById(R.id.txtEmptyKomen)

        // ===== Setup RecyclerView =====
        rvBahan.layoutManager    = LinearLayoutManager(this)
        rvLangkah.layoutManager  = LinearLayoutManager(this)
        rvKomentar.layoutManager = LinearLayoutManager(this)

        // ===== KODE LAMA: ambil data food dari Intent =====
        val food = intent.getSerializableExtra("Extra_Food") as? Food

        if (food != null) {

            foodId = food.id

            supportActionBar?.title = food.title
            imgHeader.setImageResource(food.image)
            txtJudul.text = food.title

            rvBahan.adapter   = TextAdapter(food.ingredients)
            rvLangkah.adapter = TextAdapter(food.steps)

            // ===== TOMBOL BACK =====
            btnBack.setOnClickListener { finish() }

            // ===== TOMBOL SHARE =====
            // Tambah update angka share di FoodDataSource
            btnShare.setOnClickListener {
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference("meta_resep_lokal")
                    .child(food.id.toString()).child("jumlahShare")
                    .setValue(com.google.firebase.database.ServerValue.increment(1))

                shareImage(food)
            }

            // ===== TOMBOL FAVORIT =====
            val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            var isFavorite = false // Status bawaan sebelum dicek

            // Siapkan jalur ke Firebase
            val refFavoritUser = if (currentUser != null) {
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference("favorit_lokal")
                    .child(currentUser.uid).child(food.id.toString())
            } else null

            val refMetaSimpan = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("meta_resep_lokal")
                .child(food.id.toString()).child("jumlahSimpan")

            // Cek favorit dari Firebase
            refFavoritUser?.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    isFavorite = snapshot.exists() // Jika data ID resep ini ada di Firebase user, berarti true
                    setFavoriteIcon(isFavorite)
                }
                override fun onCancelled(error: DatabaseError) {}
            })

            // tombol favorit ditekan
            btnFavorite.setOnClickListener {
                if (currentUser == null) {
                    Toast.makeText(this@Detail_Resep, "Harap login untuk menyimpan resep", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (isFavorite) {
                    refFavoritUser?.removeValue() // Hapus dari daftar
                    refMetaSimpan.setValue(com.google.firebase.database.ServerValue.increment(-1)) // Kurangi angka total
                    Toast.makeText(this@Detail_Resep, "Dihapus Dari Favorit", Toast.LENGTH_SHORT).show()
                } else {
                    refFavoritUser?.setValue(true) // Masukkan ke daftar
                    refMetaSimpan.setValue(com.google.firebase.database.ServerValue.increment(1)) // Tambah angka total
                    Toast.makeText(this@Detail_Resep, "Tersimpan di Favorit", Toast.LENGTH_SHORT).show()
                }
            }
            // ===== KOMENTAR: hubungkan id =====
            komentarAdapter = KomentarAdapter(mutableListOf())
            rvKomentar.adapter = komentarAdapter

            val idResepAplikasi = food.id.toString()
            dbKomentarRef = com.google.firebase.database.FirebaseDatabase.getInstance()
                .getReference("komentar").child(idResepAplikasi)

            dbKomentarRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener{
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val listKomen = mutableListOf<Komentar>()
                    for (child in snapshot.children){
                        val komen = child.getValue(Komentar::class.java)
                        if (komen != null) listKomen.add(komen)
                    }

                    if (listKomen.isEmpty()){
                        txtEmptyKomen.visibility = View.VISIBLE
                        rvKomentar.visibility = View.GONE
                    } else {
                        txtEmptyKomen.visibility = View.GONE
                        rvKomentar.visibility = View.VISIBLE
                        komentarAdapter.updateData(listKomen)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            // ===== KOMENTAR: tombol kirim =====
            btnKirimKomen.setOnClickListener { kirimKomentar() }

            // Scroll ke komentar kalau dibuka dari tombol komentar di Home
            if (intent.getBooleanExtra("SCROLL_TO_KOMENTAR", false)) {
                findViewById<androidx.core.widget.NestedScrollView>(R.id.nestedScroll).post {
                    findViewById<androidx.core.widget.NestedScrollView>(R.id.nestedScroll)
                        .smoothScrollTo(0, rvKomentar.bottom)
                }
            }

        } else {
            Toast.makeText(this, "Resep gagal dimuat", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    // ===== KOMENTAR: fungsi kirim =====
    private fun kirimKomentar() {
        val isiKomen = editKomentar.text.toString().trim()
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

        if (isiKomen.isEmpty()) {
            Toast.makeText(this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentUser != null){
            val komentarIdBaru = dbKomentarRef.push().key ?: return

            val komentarBaru = Komentar(
                komentarId = komentarIdBaru,
                userId = currentUser.uid,
                namaUser = currentUser.displayName ?: "User",
                isiKomentar = isiKomen,
                timestamp = System.currentTimeMillis() // Waktu
            )

            dbKomentarRef.child(komentarIdBaru).setValue(komentarBaru).addOnSuccessListener {
                editKomentar.text.clear()
                editKomentar.clearFocus()
                Toast.makeText(this, "Komentar Terkirim", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Login dahulu sebelum berkomentar", Toast.LENGTH_SHORT).show()
        }
    }

    // ===== ICON FAVORIT =====
    private fun setFavoriteIcon(isFav: Boolean) {
        if (isFav) {
            btnFavorite.setImageResource(R.drawable.bookmark_white_filled)
        } else {
            btnFavorite.setImageResource(R.drawable.bookmark_1)
        }
    }

    // ===== SHARE IMAGE =====
    private fun shareImage(food: Food) {
        try {
            val bitmap = Bitmap.createBitmap(
                layouContent.width, layouContent.height, Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            layouContent.draw(canvas)

            val cachePath = File(cacheDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/FullResep.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri: Uri = FileProvider.getUriForFile(
                this, "id.ac.pnm.food_recipe_app.provider",
                File(cachePath, "FullResep.png")
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                setDataAndType(contentUri, contentResolver.getType(contentUri))
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
                clipData = android.content.ClipData.newRawUri(null, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Bagikan Resep"))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_LONG).show()
        }
    }
}