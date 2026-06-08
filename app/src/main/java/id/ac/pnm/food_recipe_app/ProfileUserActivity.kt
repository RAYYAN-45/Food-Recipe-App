package id.ac.pnm.food_recipe_app

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_user2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private lateinit var imgProfile: ImageView
    private lateinit var txtNamaUser: TextView
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        imgProfile = findViewById(R.id.imgProfile)
        txtNamaUser = findViewById(R.id.txtNamaUser)
        btnBack = findViewById(R.id.btnBack)

        val namaUser = intent.getStringExtra("NAMA_USER")
        val fotoUser = intent.getIntExtra("FOTO_USER", 0)

        txtNamaUser.text = namaUser

        if (fotoUser != 0) {
            imgProfile.setImageResource(fotoUser)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}