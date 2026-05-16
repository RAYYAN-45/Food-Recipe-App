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

        adapter = FoodAdapter(
            foodList = FoodDataSource.getAllFoods(),

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
            onSimpanClick = { _ -> },

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

    // Refresh angka & status bookmark saat kembali ke Home
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}