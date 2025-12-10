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
    // RecyclerView untuk list makanan
    private lateinit var recyclerFood: RecyclerView
    // Adapter untuk menghubungkan data & tampilan RecyclerView
    private lateinit var adapter: FoodAdapter
    // menyimpan semua data makanan (dipakai untuk search)
    private lateinit var searchField: EditText

    private var originalFoodList = FoodDataSource.getAllFoods() // untuk search

    override fun onCreateView( //mengubah file XML menjadi tampilan nyata (View)
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // menghubungkan fragment dengan file XML fragment_home_
        return inflater.inflate(R.layout.fragment_home_, container, false)
    }
//onViewCreated() dipanggil setelah layout berhasil dibuat (inflate)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// hubungkan id RecyclerView dan SearchField dengan XML
        recyclerFood = view.findViewById(R.id.recyclerFood)
        searchField = view.findViewById(R.id.editSearch)
// agar list ditampilkan vertikal
        recyclerFood.layoutManager = LinearLayoutManager(requireContext())

        setupAdapter()  // pasang adapter ke RecyclerView
        setupSearchBar() // aktifkan pencarian
    }
    // memasukkan data ke dalam adapter
    private fun setupAdapter() {
        adapter = FoodAdapter(
            foodList = originalFoodList,
            onItemClick = { food ->
                // Klik Card → buka Detail_Resep
                val intent = Intent(requireContext(), Detail_Resep::class.java)
                intent.putExtra("Extra_Food", food)
                startActivity(intent)
            },// ketika favorit diklik
            onFavoriteClick = { food ->
                // ubah status favorite
                FoodDataSource.toggleFavorite(food.id)
                // refresh data pada adapter
                adapter.updateData(FoodDataSource.getAllFoods())
                // update list pencarian
                originalFoodList = FoodDataSource.getAllFoods()
            }
        )

        // menghubungkan recyclerView dengan adapter
        recyclerFood.adapter = adapter
    }
    // aktifkan fitur pencarian realtime
    private fun setupSearchBar() {
        // add listener setiap user mengetik
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            // realtime filter list
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
        })
    }
    // proses penyaringan list sesuai kata kunci
    private fun filterList(query: String) {
        // filter berdasarkan judul atau deskripsi
        val filtered = originalFoodList.filter { food ->
            food.title.contains(query, ignoreCase = true) ||
                    food.desc.contains(query, ignoreCase = true)
        }// tampilkan hasil filter
        adapter.updateData(filtered)
    }
    // dijalankan ketika kembali ke Home
    override fun onResume() {
        super.onResume()
        // refresh ulang data makanan
        adapter.updateData(FoodDataSource.getAllFoods())
        originalFoodList = FoodDataSource.getAllFoods()
    }
}
