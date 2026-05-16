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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    // ===== TAMBAHAN BARU: KOMENTAR =====
    private lateinit var rvKomentar: RecyclerView
    private lateinit var editKomentar: EditText
    private lateinit var btnKirimKomen: ImageButton
    private lateinit var txtEmptyKomen: TextView
    private lateinit var komentarAdapter: KomentarAdapter
    private val listKomentar = mutableListOf<Komentar>()

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

        // ===== TAMBAHAN BARU: hubungkan id komentar =====
        rvKomentar    = findViewById(R.id.rvKomentar)
        editKomentar  = findViewById(R.id.editKomentar)
        btnKirimKomen = findViewById(R.id.btnKirimKomen)
        txtEmptyKomen = findViewById(R.id.txtEmptyKomen)

        // ===== KODE LAMA: setup RecyclerView =====
        rvBahan.layoutManager   = LinearLayoutManager(this)
        rvLangkah.layoutManager = LinearLayoutManager(this)

        // ===== TAMBAHAN BARU: setup RecyclerView komentar =====
        komentarAdapter = KomentarAdapter(listKomentar)
        rvKomentar.layoutManager = LinearLayoutManager(this)
        rvKomentar.adapter = komentarAdapter

        // ===== KODE LAMA: ambil data food dari Intent =====
        val food = intent.getSerializableExtra("Extra_Food") as? Food

        if (food != null) {

            // Set judul di action bar
            supportActionBar?.title = food.title

            // Set gambar header
            imgHeader.setImageResource(food.image)

            // Set judul utama
            txtJudul.text = food.title

            // Tampilkan data bahan dan langkah
            rvBahan.adapter   = TextAdapter(food.ingredients)
            rvLangkah.adapter = TextAdapter(food.steps)

            // Tombol kembali ke halaman sebelumnya
            btnBack.setOnClickListener {
                finish()
            }

            // Tombol Share memanggil fungsi shareImage
            btnShare.setOnClickListener {
                shareImage(food)
            }

            // Cek status favorit pertama kali
            var isFavorite = FoodDataSource.isFavorite(food.id)
            setFavoriteIcon(isFavorite)

            // Ketika tombol favorite ditekan
            btnFavorite.setOnClickListener {
                FoodDataSource.toggleFavorite(food.id)
                isFavorite = FoodDataSource.isFavorite(food.id)
                setFavoriteIcon(isFavorite)

                if (isFavorite) {
                    Toast.makeText(this, "Tersimpan di favorit", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Dihapus dari favorit", Toast.LENGTH_LONG).show()
                }
            }

            // ===== TAMBAHAN BARU: kirim komentar =====
            btnKirimKomen.setOnClickListener {
                kirimKomentar()
            }

            // Scroll ke komentar kalau dari tombol komentar di Home
            val scrollToKomen = intent.getBooleanExtra("SCROLL_TO_KOMENTAR", false)
            if (scrollToKomen) {
                findViewById<androidx.core.widget.NestedScrollView>(R.id.nestedScroll)
                    .post {
                        findViewById<androidx.core.widget.NestedScrollView>(R.id.nestedScroll)
                            .smoothScrollTo(0, rvKomentar.bottom)
                    }
            }

        } else {
            Toast.makeText(this, "Resep gagal dimuat", Toast.LENGTH_LONG).show()
        }
    }

    // ===== TAMBAHAN BARU: fungsi kirim komentar =====
    private fun kirimKomentar() {
        val isiKomen = editKomentar.text.toString().trim()

        if (isiKomen.isEmpty()) {
            Toast.makeText(this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        // Buat objek komentar baru
        val waktuSekarang = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
            .format(Date())

        val komentar = Komentar(
            nama  = "Saya",         // nanti bisa diganti nama user dari login
            isi   = isiKomen,
            waktu = waktuSekarang
        )

        // Tambah ke adapter
        komentarAdapter.tambahKomentar(komentar)

        // Scroll ke atas (komentar terbaru di atas)
        rvKomentar.scrollToPosition(0)

        // Kosongkan EditText
        editKomentar.setText("")

        // Sembunyikan empty state
        txtEmptyKomen.visibility = View.GONE

        Toast.makeText(this, "Komentar terkirim!", Toast.LENGTH_SHORT).show()
    }

    // ===== KODE LAMA: fungsi icon favorit =====
    private fun setFavoriteIcon(isFav: Boolean) {
        if (isFav) {
            btnFavorite.setImageResource(R.drawable.ic_star_filled)
        } else {
            btnFavorite.setImageResource(R.drawable.ic_star_border)
        }
    }

    // ===== KODE LAMA: fungsi share image =====
    private fun shareImage(food: Food) {
        try {
            val view = layouContent

            val bitmap = Bitmap.createBitmap(
                view.width,
                view.height,
                Bitmap.Config.ARGB_8888
            )

            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            view.draw(canvas)

            val cachePath = File(cacheDir, "images")
            cachePath.mkdirs()

            val stream = FileOutputStream("$cachePath/FullResep.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val newFile = File(cachePath, "FullResep.png")
            val contentUri: Uri = FileProvider.getUriForFile(
                this,
                "id.ac.pnm.food_recipe_app.provider",
                newFile
            )

            if (contentUri != null) {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.setDataAndType(contentUri, contentResolver.getType(contentUri))
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                shareIntent.type = "image/png"
                shareIntent.clipData = android.content.ClipData.newRawUri(null, contentUri)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(shareIntent, "Bagikan Resep"))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_LONG).show()
        }
    }
}