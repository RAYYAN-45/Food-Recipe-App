package id.ac.pnm.food_recipe_app

import TextAdapter
import android.content.Intent
import android.graphics.Bitmap
import android.media.session.MediaSession
import android.net.Uri
import android.os.Bundle
import android.view.View
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

class Detail_Resep : AppCompatActivity() {

    // Ngambil view dari layout
    private lateinit var imgHeader: ImageView
    private lateinit var txtJudul: TextView
    private lateinit var rvBahan: RecyclerView
    private lateinit var rvLangkah: RecyclerView
    private lateinit var btnBack: View
    private lateinit var btnShare: View
    private lateinit var btnFavorite: ImageView
    private lateinit var layouContent: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_resep)

        // Hubungkan id layout ke variable Kotlin
        imgHeader = findViewById(R.id.imgHeader)
        txtJudul = findViewById(R.id.txtJudul)
        rvBahan = findViewById(R.id.rvBahan)
        rvLangkah = findViewById(R.id.rvLangkah)
        btnBack = findViewById(R.id.btnBack)
        btnShare = findViewById(R.id.btnShare)
        btnFavorite = findViewById(R.id.btnFavorite)
        layouContent = findViewById(R.id.layoutContent)

        // Recycler list dibuat vertical
        rvBahan.layoutManager = LinearLayoutManager(this)
        rvLangkah.layoutManager = LinearLayoutManager(this)

        // Ambil data food dari Intent
        val food = intent.getSerializableExtra("Extra_Food") as? Food

        if (food != null){

            // Set judul di action bar
            supportActionBar?.title = food.title

            // Set gambar header
            imgHeader.setImageResource(food.image)

            // Set judul utama
            txtJudul.text = food.title

            // Tampilkan data bahan dan langkah
            rvBahan.adapter = TextAdapter(food.ingredients)
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

                // Toggle nyala / mati favorit
                FoodDataSource.toggleFavorite(food.id)

                // Update status terbaru
                isFavorite = FoodDataSource.isFavorite(food.id)

                // Update icon nya
                setFavoriteIcon(isFavorite)

                // Tampilkan pemberitahuan
                if (isFavorite){
                    Toast.makeText(this, "Tersimpan di favorit", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Dihapus dari favorit", Toast.LENGTH_LONG).show()
                }
            }

        } else {
            Toast.makeText(this, "Resep gagal dimuat", Toast.LENGTH_LONG).show()
        }
    }

    // Ubah icon bintang sesuai status favorit
    private fun setFavoriteIcon(isFav: Boolean){
        if (isFav){
            btnFavorite.setImageResource(R.drawable.ic_star_filled)
        } else {
            btnFavorite.setImageResource(R.drawable.ic_star_border)
        }
    }

    // Fungsi Share (screenshot layout lalu share gambar-nya)
    private fun shareImage(food: Food){
        try {
            val view = layouContent

            // Buat screenshot dari layout
            val bitmap = Bitmap.createBitmap(
                view.width,
                view.height,
                Bitmap.Config.ARGB_8888
            )

            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            view.draw(canvas)

            // Simpan gambar ke cache
            val cachePath = File(cacheDir, "images")
            cachePath.mkdirs()

            val stream = FileOutputStream("$cachePath/FullResep.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            // Convert jadi URI biar bisa dishare
            val newFile = File(cachePath, "FullResep.png")
            val contentUri: Uri = FileProvider.getUriForFile(
                this,
                "id.ac.pnm.food_recipe_app.provider",
                newFile
            )

            // Intent share
            if (contentUri != null){

                val shareIntent = Intent(Intent.ACTION_SEND)

                shareIntent.setDataAndType(contentUri, contentResolver.getType(contentUri))
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                shareIntent.type = "image/png"
                shareIntent.clipData = android.content.ClipData.newRawUri(null, contentUri)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                // Munculkan menu Share Android
                startActivity(Intent.createChooser(shareIntent,"Bagikan Resep"))
            }

        } catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_LONG).show()
        }
    }
}

