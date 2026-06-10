package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import id.ac.pnm.food_recipe_app.data.local.database.FoodDatabase
import id.ac.pnm.food_recipe_app.data.local.toFood
import kotlinx.coroutines.launch

class FavoritFragment : Fragment() {

    private lateinit var rvFavoritLokal: RecyclerView
    private lateinit var rvFavoritKomunitas: RecyclerView
    private lateinit var txtEmptyFavorite: TextView
    private lateinit var titleLokal: TextView
    private lateinit var titleKomunitas: TextView

    private lateinit var adapterLokal: FoodAdapter
    private lateinit var adapterKomunitas: PostinganAdapter

    private var isLokalEmpty = true
    private var isKomunitasEmpty = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvFavoritLokal = view.findViewById(R.id.rvFavoritLokal)
        rvFavoritKomunitas = view.findViewById(R.id.rvFavoritKomunitas)
        txtEmptyFavorite = view.findViewById(R.id.txtEmptyFavorite)
        titleLokal = view.findViewById(R.id.titleLokal)
        titleKomunitas = view.findViewById(R.id.titleKomunitas)

        rvFavoritLokal.layoutManager = LinearLayoutManager(requireContext())
        rvFavoritKomunitas.layoutManager = LinearLayoutManager(requireContext())

        setupAdapterLokal()
        setupAdapterKomunitas()
    }

    override fun onResume() {
        super.onResume()
        tarikDataFavorit()
    }

    private fun tarikDataFavorit() {
        val db = FoodDatabase.getDatabase(requireContext())
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            lifecycleScope.launch {
                val localFavs = db.foodDao().getFavoriteFoods().map { it.toFood() }
                val localFavIds = localFavs.map { it.id }.toSet()

                adapterLokal.updateData(localFavs, localFavIds)
                adapterKomunitas.updateData(emptyList(), emptySet())

                isLokalEmpty = localFavs.isEmpty()
                isKomunitasEmpty = true
                cekEmptyState()
            }
            return
        }

        val rootRef = FirebaseDatabase.getInstance().reference
        val userId = currentUser.uid

        rootRef.child("favorit_lokal").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lifecycleScope.launch {
                    val cloudLocalFavs = snapshot.children.mapNotNull { it.key?.toIntOrNull() }
                    for (id in cloudLocalFavs) {
                        db.foodDao().updateFavorite(id, true)
                    }

                    val localFavs = db.foodDao().getFavoriteFoods().map { it.toFood() }
                    val localFavIds = localFavs.map { it.id }.toSet()

                    adapterLokal.updateData(localFavs, localFavIds)

                    isLokalEmpty = localFavs.isEmpty()
                    cekEmptyState()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        rootRef.child("favorit_postingan").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(favSnapshot: DataSnapshot) {
                val savedIds = favSnapshot.children.mapNotNull { it.key }.toSet()

                if (savedIds.isEmpty()) {
                    adapterKomunitas.updateData(emptyList(), emptySet())
                    isKomunitasEmpty = true
                    cekEmptyState()
                    return
                }

                rootRef.child("postingan").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(postSnapshot: DataSnapshot) {
                        val listKomunitas = mutableListOf<Postingan>()
                        for (child in postSnapshot.children) {
                            val post = child.getValue(Postingan::class.java)
                            if (post != null && savedIds.contains(post.postId)) {
                                listKomunitas.add(post)
                            }
                        }
                        adapterKomunitas.updateData(listKomunitas, savedIds)

                        isKomunitasEmpty = listKomunitas.isEmpty()
                        cekEmptyState()
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun cekEmptyState() {
        if (isLokalEmpty && isKomunitasEmpty) {
            txtEmptyFavorite.visibility = View.VISIBLE
            rvFavoritLokal.visibility = View.GONE
            rvFavoritKomunitas.visibility = View.GONE
            titleLokal.visibility = View.GONE
            titleKomunitas.visibility = View.GONE
        } else {
            txtEmptyFavorite.visibility = View.GONE

            rvFavoritLokal.visibility = if (isLokalEmpty) View.GONE else View.VISIBLE
            titleLokal.visibility = if (isLokalEmpty) View.GONE else View.VISIBLE

            rvFavoritKomunitas.visibility = if (isKomunitasEmpty) View.GONE else View.VISIBLE
            titleKomunitas.visibility = if (isKomunitasEmpty) View.GONE else View.VISIBLE
        }
    }

    private fun setupAdapterLokal() {
        val db = FoodDatabase.getDatabase(requireContext())
        adapterLokal = FoodAdapter(
            foodList = emptyList(),
            savedIds = emptySet(),
            onItemClick = { food ->
                val intent = Intent(requireContext(), Detail_Resep::class.java)
                intent.putExtra("Extra_Food", food)
                startActivity(intent)
            },
            onKomentarClick = { food ->
                val intent = Intent(requireContext(), Detail_Resep::class.java)
                intent.putExtra("Extra_Food", food)
                intent.putExtra("SCROLL_TO_KOMENTAR", true)
                startActivity(intent)
            },
            onSimpanClick = { food , isCurrentlySaved->
                lifecycleScope.launch {
                    val newFavorite = !isCurrentlySaved
                    db.foodDao().updateFavorite(food.id, newFavorite)

                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        val ref = FirebaseDatabase.getInstance().reference.child("favorit_lokal").child(currentUser.uid).child(food.id.toString())

                        val refMetaSimpan = FirebaseDatabase.getInstance().getReference("meta_resep_lokal")
                            .child(food.id.toString()).child("jumlahSimpan")

                        if (newFavorite) {
                            ref.setValue(true)
                            refMetaSimpan.setValue(com.google.firebase.database.ServerValue.increment(1))
                        } else {
                            ref.removeValue()
                            refMetaSimpan.setValue(com.google.firebase.database.ServerValue.increment(-1))
                        }
                    }
                    tarikDataFavorit()
                }
            },

        )
        rvFavoritLokal.adapter = adapterLokal
    }

    private fun setupAdapterKomunitas() {
        adapterKomunitas = PostinganAdapter(
            list = mutableListOf(),
            savedIds = emptySet(),
            onItemClick = { postingan ->
                val intent = Intent(requireContext(), DetailPostinganActivity::class.java)
                intent.putExtra("Extra_Postingan", postingan)
                startActivity(intent)
            },
            onBookmarkClick = { postingan ->
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val ref = FirebaseDatabase.getInstance().reference.child("favorit_postingan")
                        .child(currentUser.uid).child(postingan.postId)

                    // Referensi ke angka jumlahSimpan di node postingan
                    val postRef = FirebaseDatabase.getInstance().getReference("postingan")
                        .child(postingan.postId).child("jumlahSimpan")

                    // Hapus dari favorit
                    ref.removeValue().addOnSuccessListener {
                        // Kurangi angka di postingan (Safe increment)
                        postRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val currentCount = snapshot.getValue(Int::class.java) ?: 0
                                if (currentCount > 0) {
                                    postRef.setValue(ServerValue.increment(-1))
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })

                        tarikDataFavorit()
                    }
                }
            }
        )
        rvFavoritKomunitas.adapter = adapterKomunitas
    }
}