package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class DetailPostinganActivity : AppCompatActivity() {

    private lateinit var databaseFavoritRef: com.google.firebase.database.DatabaseReference
    private lateinit var firebaseAuth: com.google.firebase.auth.FirebaseAuth
    private var isFavorit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)

        // Ambil data postingan dari intent
        val postingan = intent.getSerializableExtra("Extra_Postingan") as? Postingan

        val btnBack       = findViewById<ImageButton>(R.id.btnBack)
        val btnFavorite   = findViewById<ImageButton>(R.id.btnFavorite)
        val btnShare      = findViewById<ImageButton>(R.id.btnShare)

        val imgHeader     = findViewById<ImageView>(R.id.imgHeader)
        val txtJudul      = findViewById<TextView>(R.id.txtJudul)
        val txtDeskripi   = findViewById<TextView>(R.id.txtDesc)
        val btnUsername   = findViewById<MaterialButton>(R.id.btnUsername)

        val rvBahan       = findViewById<RecyclerView>(R.id.rvBahan)
        val rvLangkah     = findViewById<RecyclerView>(R.id.rvLangkah)
        val rvKomentar    = findViewById<RecyclerView>(R.id.rvKomentar)

        val txtEmptyKomen = findViewById<TextView>(R.id.txtEmptyKomen)
        val editKomentar  = findViewById<EditText>(R.id.editKomentar)
        val btnKirimKomen = findViewById<ImageButton>(R.id.btnKirimKomen)
        val imgProfilUser = findViewById<ImageView>(R.id.imgProfilUser)

        if (postingan != null) {
            txtJudul.text = postingan.judulResep
            btnUsername.text = postingan.namaUser
            txtDeskripi.text = postingan.deskripsi


            val stringBahan = postingan.bahan
            val listBahan: List<String> = stringBahan.split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            val stringlangkah = postingan.langkah
            val listLangkah: List<String> = stringlangkah.split("\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            rvBahan.layoutManager = LinearLayoutManager(this)
            rvBahan.adapter = BahanLangkahAdapter(listBahan, isLangkah = false)

            rvLangkah.layoutManager = LinearLayoutManager(this)
            rvLangkah.adapter = BahanLangkahAdapter(listLangkah, isLangkah = true)

            rvKomentar.layoutManager = LinearLayoutManager(this)
            val komentarAdapter = KomentarAdapter(mutableListOf())
            rvKomentar.adapter = komentarAdapter

            val dbKomentarRef = FirebaseDatabase.getInstance().getReference("komentar").child(postingan.postId)

            dbKomentarRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listKomentar = mutableListOf<Komentar>()
                    for (child in snapshot.children){
                        val komen = child.getValue(Komentar::class.java)
                        if (komen != null) listKomentar.add(komen)
                    }

                    if (listKomentar.isEmpty()){
                        txtEmptyKomen.visibility = View.VISIBLE
                        rvKomentar.visibility = View.GONE
                    } else {
                        txtEmptyKomen.visibility = View.GONE
                        rvKomentar.visibility = View.VISIBLE
                        komentarAdapter.updateData(listKomentar)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            btnKirimKomen.setOnClickListener {
                val isi = editKomentar.text.toString().trim()
                val currentUser = FirebaseAuth.getInstance().currentUser

                if (isi.isEmpty()){
                    Toast.makeText(this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (currentUser != null){
                    val komentarIdBaru = dbKomentarRef.push().key ?: return@setOnClickListener

                    val komentarBaru = Komentar(
                        komentarId = komentarIdBaru,
                        userId = currentUser.uid,
                        namaUser = currentUser.displayName ?: "User",
                        isiKomentar = isi,
                        timestamp = System.currentTimeMillis()
                    )

                    dbKomentarRef.child(komentarIdBaru).setValue(komentarBaru).addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("postingan").child(postingan.postId)
                            .child("jumlahKomen").setValue(com.google.firebase.database.ServerValue.increment(1))

                        editKomentar.text.clear()
                        editKomentar.clearFocus()
                        rvKomentar.scrollToPosition(komentarAdapter.itemCount)
                        Toast.makeText(this, "Komentar Terkirim", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Harus login untuk berkomentar", Toast.LENGTH_SHORT).show()
                }
            }

        }

        btnBack.setOnClickListener { finish() }

        btnUsername.setOnClickListener {
            if (postingan != null) {
                val intent = Intent(this, ProfileUserActivity::class.java)
                intent.putExtra("USER_ID_PEMILIK", postingan.userId)
                intent.putExtra("NAMA_USER_PEMILIK", postingan.namaUser)
                startActivity(intent)
            }
        }

        if (postingan != null){
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null){
                val favRef = FirebaseDatabase.getInstance().getReference("favorit_postingan").child(currentUser.uid).child(postingan.postId)

                favRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        isFavorit =snapshot.exists()
                        if (isFavorit){
                            btnFavorite.setImageResource(R.drawable.bookmark_white_filled)
                        } else {
                            btnFavorite.setImageResource(R.drawable.bookmark_1)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

                btnFavorite.setOnClickListener {
                    val databasePostRef = FirebaseDatabase.getInstance().getReference("postingan").child(postingan.postId)
                    isFavorit = !isFavorit

                    if (isFavorit){
                        btnFavorite.setImageResource(R.drawable.bookmark_white_filled)
                        favRef.setValue(true)

                        databasePostRef.child("jumlahSimpan").setValue(com.google.firebase.database.ServerValue.increment(1))
                        Toast.makeText(this@DetailPostinganActivity, "Berhasil Disimpan di Favorit", Toast.LENGTH_SHORT).show()
                    } else {
                        btnFavorite.setImageResource(R.drawable.bookmark_1)
                        favRef.removeValue()
                        databasePostRef.child("jumlahSimpan").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val currentCount = snapshot.getValue(Int::class.java) ?: 0
                                if (currentCount > 0) {
                                    databasePostRef.child("jumlahSimpan").setValue(ServerValue.increment(-1))
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                        Toast.makeText(this@DetailPostinganActivity, "Dihapus dari Favorit", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                btnFavorite.setOnClickListener {
                    Toast.makeText(this@DetailPostinganActivity, "Harap Login Dahulu", Toast.LENGTH_SHORT).show()
                }
            }

            btnShare.setOnClickListener {
                if (postingan != null) {
                    FirebaseDatabase.getInstance().getReference("postingan")
                        .child(postingan.postId).child("jumlahShare")
                        .setValue(com.google.firebase.database.ServerValue.increment(1))

                    shareImage(postingan)
                }
            }
        }
    }

    private fun shareImage(postingan: Postingan) {
        try {
            val layoutContent = findViewById<View>(R.id.layoutContent)

            val bitmap = android.graphics.Bitmap.createBitmap(
                layoutContent.width, layoutContent.height, android.graphics.Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            layoutContent.draw(canvas)

            val cachePath = java.io.File(cacheDir, "images")
            cachePath.mkdirs()
            val stream = java.io.FileOutputStream("$cachePath/FullPostingan.png")
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri: android.net.Uri = androidx.core.content.FileProvider.getUriForFile(
                this, "id.ac.pnm.food_recipe_app.provider",
                java.io.File(cachePath, "FullPostingan.png")
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                setDataAndType(contentUri, contentResolver.getType(contentUri))
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Bagikan Postingan"))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_LONG).show()
        }
    }
}