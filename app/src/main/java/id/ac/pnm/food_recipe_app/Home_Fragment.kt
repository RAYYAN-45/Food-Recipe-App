package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        // Inisialisasi adapter dengan 5 lambda/callback
        adapter = FoodAdapter(
            foodList = FoodDataSource.getFoodList(),

            // 1. Klik card → buka Detail_Resep
            onItemClick = { food ->
                val intent = Intent(requireContext(), Detail_Resep::class.java)
                intent.putExtra("FOOD_DATA", food)
                startActivity(intent)
            },

            // 2. Klik komentar → buka Detail_Resep, scroll ke komentar
            onKomentarClick = { food ->
                val intent = Intent(requireContext(), Detail_Resep::class.java)
                intent.putExtra("FOOD_DATA", food)
                intent.putExtra("SCROLL_TO_KOMENTAR", true)
                startActivity(intent)
            },

            // 3. Klik simpan → toggle bookmark (sudah dihandle di adapter)
            //    Kalau mau simpan ke FoodDataSource juga, tambahkan di sini
            onSimpanClick = { food ->
                FoodDataSource.toggleFavorite(food.id)
            },

            // 4. Klik share → buka Android share sheet
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