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
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        recyclerFavorite.layoutManager = LinearLayoutManager(requireContext())

        // Tombol Back → kembali ke Home + update BottomNav
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.Frame_Layout, Home_Fragment())
                .commit()

            val bottomNav = requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNav.selectedItemId = R.id.Home
        }

        setupAdapter()
    }

    private fun setupAdapter() {
        adapter = FoodAdapter(
            foodList = FoodDataSource.getFavoriteFoods(),
            onItemClick = { food ->
                // Klik Card → buka Detail_Resep
                val intent = Intent(requireContext(), Detail_Resep::class.java)
                startActivity(intent)
            },
            onFavoriteClick = { food ->
                FoodDataSource.toggleFavorite(food.id)
                adapter.updateData(FoodDataSource.getFavoriteFoods())
            }
        )

        recyclerFavorite.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.updateData(FoodDataSource.getFavoriteFoods())
    }
}
