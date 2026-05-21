package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoritFragment : Fragment() {

    private lateinit var recyclerFavorite: RecyclerView
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerFavorite = view.findViewById(R.id.recyclerFavorite)

        recyclerFavorite.layoutManager = LinearLayoutManager(requireContext())
        
        setupAdapter()
    }

    private fun setupAdapter() {
        adapter = FoodAdapter(
            foodList = FoodDataSource.getFavoriteFoods(),

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

            // Klik simpan → toggle favorit lalu refresh list
            // (angka & ikon sudah dihandle di adapter)
            onSimpanClick = { _ ->
                adapter.updateData(FoodDataSource.getFavoriteFoods())
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

        recyclerFavorite.adapter = adapter
    }

    // Refresh data favorit + status bookmark setiap kali fragment aktif
    override fun onResume() {
        super.onResume()
        adapter.updateData(FoodDataSource.getFavoriteFoods())
    }
}