package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Home_Fragment : Fragment() {

    private lateinit var recyclerFood: RecyclerView
    private lateinit var adapter: FoodAdapter
    private lateinit var searchField: EditText

    // DATA ASLI (baru ditambah utk search)
    private var originalFoodList = FoodDataSource.getAllFoods()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerFood = view.findViewById(R.id.recyclerFood)
        searchField = view.findViewById(R.id.editSearch)

        recyclerFood.layoutManager = LinearLayoutManager(requireContext())

        setupAdapter()
        setupSearchBar()   // 🔥 BARU: aktifkan fitur search
    }

    private fun setupAdapter() {
        adapter = FoodAdapter(
            foodList = originalFoodList,
            onItemClick = { food ->
                val intent = Intent(requireContext(), Detail_Resep::class.java)
                intent.putExtra("FOOD_DATA", food)
                startActivity(intent)
            },
            onFavoriteClick = { food ->
                FoodDataSource.toggleFavorite(food.id)

                // Update adapter
                adapter.updateData(FoodDataSource.getAllFoods())

                // update data asli supaya search tetap valid
                originalFoodList = FoodDataSource.getAllFoods()
            }
        )

        recyclerFood.adapter = adapter
    }

    // 🔍 BARU: Fungsi search
    private fun setupSearchBar() {
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
        })
    }

    // 🔍 BARU: Filter data
    private fun filterList(query: String) {
        val filtered = originalFoodList.filter { food ->
            food.title.contains(query, ignoreCase = true)
                    || food.desc.contains(query, ignoreCase = true)
        }

        adapter.updateData(filtered)
    }

    override fun onResume() {
        super.onResume()
        adapter.updateData(FoodDataSource.getAllFoods())
        originalFoodList = FoodDataSource.getAllFoods() // BARU: update untuk search
    }
}
