package id.ac.pnm.food_recipe_app

import TextAdapter
import android.content.Intent
import android.media.session.MediaSession
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Detail_Resep : AppCompatActivity() {

    private lateinit var imgHeader: ImageView
    private lateinit var txtJudul: TextView
    private lateinit var rvBahan: RecyclerView
    private lateinit var rvLangkah: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_resep)

        imgHeader = findViewById<ImageView>(R.id.imgHeader)
        txtJudul = findViewById<TextView>(R.id.txtJudul)
        rvBahan = findViewById(R.id.rvBahan)
        rvLangkah = findViewById(R.id.rvLangkah)

        rvBahan.layoutManager = LinearLayoutManager(this)
        rvLangkah.layoutManager = LinearLayoutManager(this)

        val food = intent.getSerializableExtra("Extra_Food") as? Food

        if (food != null){
            supportActionBar?.title = food.title
            imgHeader.setImageResource(food.image)
            txtJudul.text = food.title
            rvBahan.adapter = TextAdapter(food.ingredients)
            rvLangkah.adapter = TextAdapter(food.steps)
        } else {
            Toast.makeText(this, "Resep Gagal Dimuat", Toast.LENGTH_LONG).show()
        }
    }
}
