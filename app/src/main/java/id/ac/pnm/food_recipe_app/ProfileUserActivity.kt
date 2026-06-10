package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileUserActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var txtNamaUser: TextView
    private lateinit var btnBack: ImageView
    private lateinit var rvPostinganUserLain: RecyclerView
    private lateinit var adapter: PostinganAdapter
    private val listPostingan = mutableListOf<Postingan>()
    private var savedIds = setOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_user2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imgProfile = findViewById(R.id.imgProfile)
        txtNamaUser = findViewById(R.id.txtNamaUser)
        btnBack = findViewById(R.id.btnBack)
        rvPostinganUserLain = findViewById(R.id.rvPostinganUserLain)

        val namaUser = intent.getStringExtra("NAMA_USER_PEMILIK")
        val userId = intent.getStringExtra("USER_ID_PEMILIK")

        txtNamaUser.text = namaUser

        btnBack.setOnClickListener {
            finish()
        }

        rvPostinganUserLain.layoutManager = LinearLayoutManager(this)
        setupAdapter()

        if(userId != null){
            tarikDataPostinganUserLain(userId)
        }
    }

    private fun setupAdapter(){
        adapter = PostinganAdapter(
            list = listPostingan,
            savedIds = savedIds,
            onItemClick = {postingan ->
                val intent = Intent(this, DetailPostinganActivity::class.java)
                intent.putExtra("Extra_Postingan", postingan)
                startActivity(intent)
            },
            onBookmarkClick = {postingan ->
                val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val ref = com.google.firebase.database.FirebaseDatabase.getInstance().reference
                        .child("favorit_postingan").child(currentUser.uid).child(postingan.postId)

                    val postRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("postingan")
                        .child(postingan.postId).child("jumlahSimpan")

                    if (savedIds.contains(postingan.postId)) {
                        // Hapus dari favorit
                        ref.removeValue()
                        savedIds = savedIds - postingan.postId

                        // Kurangi angka
                        postRef.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                                val currentCount = snapshot.getValue(Int::class.java) ?: 0
                                if (currentCount > 0) postRef.setValue(com.google.firebase.database.ServerValue.increment(-1))
                            }
                            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
                        })
                        android.widget.Toast.makeText(this, "Dihapus dari favorit", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        // Simpan ke favorit
                        ref.setValue(true)
                        savedIds = savedIds + postingan.postId

                        // Tambah angka
                        postRef.setValue(com.google.firebase.database.ServerValue.increment(1))
                        android.widget.Toast.makeText(this, "Disimpan ke favorit", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    adapter.updateData(listPostingan, savedIds)
                }
            },
            onDeleteClick = null
        )
        rvPostinganUserLain.adapter = adapter
    }

    private fun tarikDataPostinganUserLain(userIdPemilik : String) {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().reference

        if (currentUser != null) {
            dbRef.child("favorit_postingan").child(currentUser.uid)
                .addListenerForSingleValueEvent(object :
                    com.google.firebase.database.ValueEventListener {
                    override fun onDataChange(favSnapshot: com.google.firebase.database.DataSnapshot) {
                        savedIds = favSnapshot.children.mapNotNull { it.key }.toSet()
                        tarikResepSesuaiUser(userIdPemilik, dbRef)
                    }

                    override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
                })
        } else {
            tarikResepSesuaiUser(userIdPemilik, dbRef)
        }
    }

    private fun tarikResepSesuaiUser(userIdPemilik: String, dbRef: com.google.firebase.database.DatabaseReference) {
        val query = dbRef.child("postingan").orderByChild("userId").equalTo(userIdPemilik)

        query.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                listPostingan.clear()
                for (child in snapshot.children) {
                    val postingan = child.getValue(Postingan::class.java)
                    if (postingan != null) {
                        listPostingan.add(0, postingan)
                    }
                }
                adapter.updateData(listPostingan, savedIds)
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }
}
