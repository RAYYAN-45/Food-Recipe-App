package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import id.ac.pnm.food_recipe_app.data.local.database.FoodDatabase
import id.ac.pnm.food_recipe_app.data.local.toFood
import kotlinx.coroutines.launch

class Home_Fragment : Fragment() {

    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerFood = view.findViewById<RecyclerView>(R.id.recyclerFood)
        recyclerFood.layoutManager = LinearLayoutManager(requireContext())

        val db = FoodDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val foodList = db.foodDao()
                .getAllFoods()
                .map { it.toFood() }

            adapter = FoodAdapter(
                foodList = foodList,

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

                // Klik simpan → sudah dihandle di adapter (toggle + update angka)
                onSimpanClick = { food ->
                    lifecycleScope.launch {
                        val newFavorite = !food.isFavorite

                        db.foodDao().updateFavorite(food.id, newFavorite)

                        val updatedList = db.foodDao()
                            .getAllFoods()
                            .map { it.toFood() }

                        adapter.updateData(updatedList)
                    }
                },

                // Klik share → buka Android share sheet
                onShareClick = { food ->
                    val shareText = "Cek resep ${food.title} di EuroCuisine!\n\n${food.desc}"
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    startActivity(Intent.createChooser(shareIntent, "Bagikan resep via..."))
                }
            )

            recyclerFood.adapter = adapter
        }
    }

    // Refresh angka & status bookmark saat kembali ke Home
    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized){
            adapter.notifyDataSetChanged()
        }
    }
}