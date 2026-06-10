package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import id.ac.pnm.food_recipe_app.data.local.database.FoodDatabase
import id.ac.pnm.food_recipe_app.data.local.toFood
import kotlinx.coroutines.launch

class Home_Fragment : Fragment() {

    private lateinit var adapter: FoodAdapter
    private lateinit var recyclerFood: RecyclerView
    private var listFavoritId = mutableSetOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerFood = view.findViewById(R.id.recyclerFood)
        recyclerFood.layoutManager = LinearLayoutManager(requireContext())

        loadData()
        tarikDataFavoritFirebase()
    }

    private fun loadData() {
        val db = FoodDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val foodList = db.foodDao()
                .getAllFoods()
                .map { it.toFood() }

            if (::adapter.isInitialized) {
                // Sudah ada adapter → update data saja supaya isFavorite ter-refresh
                adapter.updateData(foodList, listFavoritId)
            } else {
                // Pertama kali → buat adapter baru
                adapter = FoodAdapter(
                    foodList = foodList,
                    savedIds = listFavoritId,

                    // Klik card → buka Detail_Resep
                    onItemClick = { food ->
                        val intent = Intent(requireContext(), Detail_Resep::class.java)
                        intent.putExtra("Extra_Food", food)
                        startActivity(intent)
                    },

                    // Klik komentar → buka Detail_Resep scroll ke komentar
                    onKomentarClick = { food ->
                        val intent = Intent(requireContext(), Detail_Resep::class.java)
                        intent.putExtra("Extra_Food", food)
                        intent.putExtra("SCROLL_TO_KOMENTAR", true)
                        startActivity(intent)
                    },

                    // Klik simpan → update Room + refresh list
                    onSimpanClick = { food, isCurrentlySaved ->
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser == null) {
                            Toast.makeText(requireContext(), "Harap login", Toast.LENGTH_SHORT).show()
                            return@FoodAdapter
                        }

                        val refFavoritUser = FirebaseDatabase.getInstance().getReference("favorit_lokal")
                            .child(currentUser.uid).child(food.id.toString())

                        val refMetaSimpan = FirebaseDatabase.getInstance().getReference("meta_resep_lokal")
                            .child(food.id.toString()).child("jumlahSimpan")

                        if (isCurrentlySaved) {
                            refFavoritUser.removeValue()
                            refMetaSimpan.setValue(com.google.firebase.database.ServerValue.increment(-1)) // TAMBAHAN: Kurangi
                            Toast.makeText(requireContext(), "Dihapus dari Favorit", Toast.LENGTH_SHORT).show()
                        } else {
                            refFavoritUser.setValue(true)
                            refMetaSimpan.setValue(com.google.firebase.database.ServerValue.increment(1)) // TAMBAHAN: Tambah
                            Toast.makeText(requireContext(), "Disimpan Ke Favorit", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                recyclerFood.adapter = adapter
            }
        }
    }

    // Setiap kali kembali ke Home (termasuk balik dari Detail_Resep)
    // reload data dari Room supaya isFavorite & ikon bookmark selalu up-to-date
    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun tarikDataFavoritFirebase() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val ref = FirebaseDatabase.getInstance().getReference("favorit_lokal").child(currentUser.uid)

        ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                listFavoritId.clear()
                for (data in snapshot.children) {
                    data.key?.toIntOrNull()?.let { listFavoritId.add(it) }
                }

                // Segarkan layar setiap kali ada data favorit baru ditarik
                if (::adapter.isInitialized && isAdded) {
                    val db = FoodDatabase.getDatabase(requireContext())
                    lifecycleScope.launch {
                        val foods = db.foodDao().getAllFoods().map { it.toFood() }
                        adapter.updateData(foods, listFavoritId)
                    }
                }
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }
}