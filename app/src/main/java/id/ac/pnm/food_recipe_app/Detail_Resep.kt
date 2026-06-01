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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_resep)

        // ===== KODE LAMA: hubungkan id layout =====
        imgHeader    = findViewById(R.id.imgHeader)
        txtJudul     = findViewById(R.id.txtJudul)
        rvBahan      = findViewById(R.id.rvBahan)
        rvLangkah    = findViewById(R.id.rvLangkah)
        btnBack      = findViewById(R.id.btnBack)
        btnShare     = findViewById(R.id.btnShare)
        btnFavorite  = findViewById(R.id.btnFavorite)
        layouContent = findViewById(R.id.layoutContent)

        // ===== KOMENTAR: hubungkan id =====
        rvKomentar    = findViewById(R.id.rvKomentar)
        editKomentar  = findViewById(R.id.editKomentar)
        btnKirimKomen = findViewById(R.id.btnKirimKomen)
        txtEmptyKomen = findViewById(R.id.txtEmptyKomen)

        // ===== KODE LAMA: setup RecyclerView =====
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

            // ===== KOMENTAR =====
            val listKomentar = FoodDataSource.getKomentar(food.id)
            komentarAdapter = KomentarAdapter(listKomentar)
            rvKomentar.adapter = komentarAdapter
            updateEmptyState()

            // ===== TOMBOL BACK =====
            btnBack.setOnClickListener { finish() }

            // ===== TOMBOL SHARE =====
            // Tambah update angka share di FoodDataSource
            btnShare.setOnClickListener {
                FoodDataSource.tambahShare(food.id) // ← update angka share
                shareImage(food)
            }

            // ===== TOMBOL FAVORIT =====
            val db = FoodDatabase.getDatabase(this)
            var isFavorite = food.isFavorite
            setFavoriteIcon(isFavorite)

            btnFavorite.setOnClickListener {
                isFavorite = !isFavorite

                // Update angka simpan di FoodDataSource
                if (isFavorite) {
                    FoodDataSource.tambahSimpan(food.id)
                } else {
                    FoodDataSource.kurangiSimpan(food.id)
                }

                // Update Room
                lifecycleScope.launch {
                    db.foodDao().updateFavorite(food.id, isFavorite)
                    runOnUiThread {
                        setFavoriteIcon(isFavorite)
                        if (isFavorite) {
                            Toast.makeText(this@Detail_Resep, "Tersimpan di Favorit", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@Detail_Resep, "Dihapus Dari Favorit", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

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
        if (foodId != -1) {
            komentarAdapter.notifyDataSetChanged()
            updateEmptyState()
        }
    }

    // ===== KOMENTAR: fungsi kirim =====
    private fun kirimKomentar() {
        val isiKomen = editKomentar.text.toString().trim()

        if (isiKomen.isEmpty()) {
            Toast.makeText(this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val waktu = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date())

        val komentar = Komentar(
            nama  = "Saya",
            isi   = isiKomen,
            waktu = waktu
        )

        FoodDataSource.tambahKomentar(foodId, komentar)
        komentarAdapter.notifyItemInserted(0)
        rvKomentar.scrollToPosition(0)
        editKomentar.setText("")
        updateEmptyState()

        Toast.makeText(this, "Komentar terkirim!", Toast.LENGTH_SHORT).show()
    }

    private fun updateEmptyState() {
        txtEmptyKomen.visibility = if (FoodDataSource.getJumlahKomenAktual(foodId) == 0) {
            View.VISIBLE
        } else {
            View.GONE
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