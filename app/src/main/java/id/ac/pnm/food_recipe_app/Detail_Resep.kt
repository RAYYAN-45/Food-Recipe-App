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

        imgHeader = findViewById<ImageView>(R.id.imgHeader)
        txtJudul = findViewById<TextView>(R.id.txtJudul)
        rvBahan = findViewById(R.id.rvBahan)
        rvLangkah = findViewById(R.id.rvLangkah)
        btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnShare = findViewById<ImageButton>(R.id.btnShare)
        btnFavorite = findViewById<ImageButton>(R.id.btnFavorite)
        layouContent = findViewById(R.id.layoutContent)

        rvBahan.layoutManager = LinearLayoutManager(this)
        rvLangkah.layoutManager = LinearLayoutManager(this)

        val food = intent.getSerializableExtra("Extra_Food") as? Food

        if (food != null){
            supportActionBar?.title = food.title
            imgHeader.setImageResource(food.image)
            txtJudul.text = food.title
            rvBahan.adapter = TextAdapter(food.ingredients)
            rvLangkah.adapter = TextAdapter(food.steps)

            btnBack.setOnClickListener {
                finish()
            }

            btnShare.setOnClickListener {
                shareImage(food)
            }

            var isFavorite = FoodDataSource.isFavorite(food.id)
            setFavoriteIcon(isFavorite)

            btnFavorite.setOnClickListener {
                FoodDataSource.toggleFavorite(food.id)
                isFavorite = FoodDataSource.isFavorite(food.id)
                setFavoriteIcon(isFavorite)

                if (isFavorite){
                    Toast.makeText(this, "Tesimpan", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Dihapus dari Favorit", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Resep Gagal Dimuat", Toast.LENGTH_LONG).show()
        }
    }

    private fun setFavoriteIcon(isFav: Boolean){
        if (isFav){
            btnFavorite.setImageResource(R.drawable.ic_star_filled)
        } else {
            btnFavorite.setImageResource(R.drawable.ic_star_border)
        }
    }

    private fun shareImage(food: Food){
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

            if (contentUri != null){
                val shareIntent = Intent(Intent.ACTION_SEND)

                shareIntent.setDataAndType(contentUri, contentResolver.getType(contentUri))
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                shareIntent.type = "image/png"
                shareIntent.clipData = android.content.ClipData.newRawUri(null, contentUri)
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                startActivity(Intent.createChooser(shareIntent,"Bagikan Resep"))

            }
        } catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this, "Gagal Memuat Gambar", Toast.LENGTH_LONG).show()
        }
    }
}
