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
    // RecyclerView untuk menampilkan daftar favorit
    private lateinit var recyclerFavorite: RecyclerView
    // Adapter makanan favorit
    private lateinit var adapter: FoodAdapter

    // Ketika Fragment dibuat (inflate layout) Inflate = mengambil layout XML lalu mengubahnya menjadi object View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // menghubungkan Fragment dengan file XML fragment_favorite
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }
    // Ketika view sudah siap digunakan
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

// hubungkan id RecyclerView di layout dan tombol back (manual karena fragment bukan activity)
        recyclerFavorite = view.findViewById(R.id.recyclerFavorite)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        // supaya list tampil ke bawah
        recyclerFavorite.layoutManager = LinearLayoutManager(requireContext())

        // Tombol Back → kembali ke Home
        btnBack.setOnClickListener {
            // mengganti isi FrameLayout dengan Home_Fragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.Frame_Layout, Home_Fragment())
                .commit()

// Update icon pada Bottom Navigation
            val bottomNav = requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            // pindahkan item bottom nav ke Home
            bottomNav.selectedItemId = R.id.Home
        }

        setupAdapter()
    }

    private fun setupAdapter() {
        // ambil list favorit lalu tampilkan
        adapter = FoodAdapter(
            foodList = FoodDataSource.getFavoriteFoods(),
            onItemClick = { food ->
                // Klik Card → buka Detail_Resep
                val intent = Intent(requireContext(), Detail_Resep::class.java)
                intent.putExtra("Extra_Food", food)
                startActivity(intent)
            },
            onFavoriteClick = { food ->
                // toggle favorite
                FoodDataSource.toggleFavorite(food.id)
                // refresh list favorite
                adapter.updateData(FoodDataSource.getFavoriteFoods())
            }
        )

        recyclerFavorite.adapter = adapter
    }
    // data favorit di-refresh
    override fun onResume() {
        super.onResume()
        adapter.updateData(FoodDataSource.getFavoriteFoods())
    }
}
