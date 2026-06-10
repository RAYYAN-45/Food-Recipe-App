package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Komun_Fragment : Fragment() {

    private lateinit var recyclerKomunitas: RecyclerView
    private lateinit var editSearch: EditText
    private lateinit var txtEmpty: TextView
    private lateinit var fabTambah: FloatingActionButton

    private lateinit var adapterKomunitas: PostinganAdapter

    private lateinit var dbRefPostingan: DatabaseReference
    private lateinit var dbRefFavorit: DatabaseReference
    private lateinit var currentUserId: String

    private var listAllPostingan = mutableListOf<Postingan>()
    private var listFilteredSearch = mutableListOf<Postingan>()
    private var listFavoritId = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_komun, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerKomunitas = view.findViewById(R.id.recyclerKomunitas)
        editSearch        = view.findViewById(R.id.editSearchKomunitas)
        txtEmpty          = view.findViewById(R.id.txtEmptyKomunitas)
        fabTambah         = view.findViewById(R.id.fabTambahPostingan)

        recyclerKomunitas.layoutManager = LinearLayoutManager(requireContext())

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        dbRefPostingan = FirebaseDatabase.getInstance().getReference("postingan")
        dbRefFavorit = FirebaseDatabase.getInstance().getReference("favorit_postingan").child(currentUserId)

        setupAdapter()
        initFirebaseListeners()

        // Search tunggu selesai
        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterSearch(s.toString())
            }
        })

        fabTambah.setOnClickListener {
            val intent = Intent(requireContext(), TambahResepActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupAdapter() {
        adapterKomunitas = PostinganAdapter(
            list = listAllPostingan,
            savedIds = listFavoritId,
            onItemClick = { postingan ->
                val intent = Intent(requireContext(), DetailPostinganActivity::class.java)
                intent.putExtra("Extra_Postingan", postingan)
                startActivity(intent)
            },
            onBookmarkClick = { postingan ->
                val databasePostRef = FirebaseDatabase.getInstance().getReference("postingan").child(postingan.postId)

                if (listFavoritId.contains(postingan.postId)) {
                    dbRefFavorit.child(postingan.postId).removeValue()

                    databasePostRef.child("jumlahSimpan").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val count = snapshot.getValue(Int::class.java) ?: 0
                            if (count > 0) databasePostRef.child("jumlahSimpan").setValue(ServerValue.increment(-1))
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                    Toast.makeText(requireContext(), "Dihapus dari Favorit", Toast.LENGTH_SHORT).show()
                } else {
                    dbRefFavorit.child(postingan.postId).setValue(true)

                    databasePostRef.child("jumlahSimpan").setValue(ServerValue.increment(1))
                    Toast.makeText(requireContext(), "Disimpan di Favorit", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerKomunitas.adapter = adapterKomunitas
    }

    private fun initFirebaseListeners() {
        // Ambil data resep dari Firebase
        dbRefPostingan.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listAllPostingan.clear()
                for (data in snapshot.children) {
                    val p = data.getValue(Postingan::class.java)
                    if (p != null) listAllPostingan.add(p)
                }
                listAllPostingan.sortByDescending { it.timestamp }
                refreshDisplay()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Ambil status bookmark user dari Firebase
        dbRefFavorit.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listFavoritId.clear()
                for (data in snapshot.children) {
                    data.key?.let { listFavoritId.add(it) }
                }
                refreshDisplay()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun refreshDisplay() {
        val query = editSearch.text.toString()
        if (query.isBlank()) {
            adapterKomunitas.updateData(listAllPostingan, listFavoritId)
            updateEmptyState(listAllPostingan.isEmpty())
        } else {
            filterSearch(query)
        }
    }

    private fun filterSearch(query: String) {
        if (query.isBlank()) {
            adapterKomunitas.updateData(listAllPostingan, listFavoritId)
            updateEmptyState(listAllPostingan.isEmpty())
            return
        }
        val q = query.lowercase()
        listFilteredSearch = listAllPostingan.filter {
            it.namaUser.lowercase().contains(q) || it.judulResep.lowercase().contains(q)
        }.toMutableList()

        adapterKomunitas.updateData(listFilteredSearch, listFavoritId)
        updateEmptyState(listFilteredSearch.isEmpty())
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            txtEmpty.text = "Tidak ada postingan ditemukan"
            txtEmpty.visibility = View.VISIBLE
            recyclerKomunitas.visibility = View.GONE
        } else {
            txtEmpty.visibility = View.GONE
            recyclerKomunitas.visibility = View.VISIBLE
        }
    }
}