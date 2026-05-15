package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter menerima:
// 1. list data
// 2. event ketika item diklik
// 3. event ketika tombol komentar diklik
// 4. event ketika tombol simpan diklik
// 5. event ketika tombol share diklik
class FoodAdapter(
    private var foodList: List<Food>,
    private val onItemClick: (Food) -> Unit,
    private val onKomentarClick: (Food) -> Unit,
    private val onSimpanClick: (Food) -> Unit,
    private val onShareClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    // Menyimpan id resep yang sudah di-bookmark
    private val bookmarkedIds = mutableSetOf<Int>()

    // ViewHolder = penampung tampilan setiap item di RecyclerView
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ambil komponen view dari layout item_food.xml
        val imgFood: ImageView           = itemView.findViewById(R.id.imgFood)
        val txtTitle: TextView           = itemView.findViewById(R.id.txtTitle)
        val txtDesc: TextView            = itemView.findViewById(R.id.txtDesc)
        val txtJumlahKomen: TextView     = itemView.findViewById(R.id.txtJumlahKomen)
        val txtJumlahSimpan: TextView    = itemView.findViewById(R.id.txtJumlahSimpan)
        val txtJumlahShare: TextView     = itemView.findViewById(R.id.txtJumlahShare)
        val imgBookmark: ImageView       = itemView.findViewById(R.id.imgBookmark)
        val layoutKomentar: LinearLayout = itemView.findViewById(R.id.layoutKomentar)
        val layoutSimpan: LinearLayout   = itemView.findViewById(R.id.layoutSimpan)
        val layoutShare: LinearLayout    = itemView.findViewById(R.id.layoutShare)
    }

    // Membuat tampilan item_food.xml untuk setiap row list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // "inflate" = mengubah XML menjadi View
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ambil data sesuai posisi
        val food = foodList[position]

        // Menampilkan data (sama seperti kode lama, pakai field yang sama)
        holder.txtTitle.text = food.title
        holder.txtDesc.text  = food.desc
        holder.imgFood.setImageResource(food.image)

        // Tampilkan status bookmark pada ikon
        val isBookmarked = bookmarkedIds.contains(food.id)
        updateBookmarkIcon(holder.imgBookmark, isBookmarked)

        // =============================================
        // Klik item → pindah ke Detail (sama seperti kode lama)
        // =============================================
        holder.itemView.setOnClickListener {
            onItemClick(food)
        }

        // =============================================
        // LISTENER 1: Komentar → callback ke fragment/activity
        // =============================================
        holder.layoutKomentar.setOnClickListener {
            onKomentarClick(food)
        }

        // =============================================
        // LISTENER 2: Simpan → toggle bookmark + callback
        // =============================================
        holder.layoutSimpan.setOnClickListener {
            val currentlyBookmarked = bookmarkedIds.contains(food.id)
            if (currentlyBookmarked) {
                bookmarkedIds.remove(food.id)
            } else {
                bookmarkedIds.add(food.id)
            }
            updateBookmarkIcon(holder.imgBookmark, !currentlyBookmarked)
            onSimpanClick(food)
        }

        // =============================================
        // LISTENER 3: Share → callback ke fragment/activity
        // =============================================
        holder.layoutShare.setOnClickListener {
            onShareClick(food)
        }
    }

    // jumlah item dalam list
    override fun getItemCount(): Int {
        return foodList.size
    }

    // dipanggil kalau ada perubahan list (sama seperti kode lama)
    fun updateData(newList: List<Food>) {
        foodList = newList
        // refresh tampilan RecyclerView
        notifyDataSetChanged()
    }

    // Helper: update ikon bookmark
    private fun updateBookmarkIcon(imgBookmark: ImageView, isBookmarked: Boolean) {
        if (isBookmarked) {
            imgBookmark.setImageResource(R.drawable.bookmark_filled)
        } else {
            imgBookmark.setImageResource(R.drawable.bookmark)
        }
    }

    // Ambil list resep yang sudah di-bookmark (untuk FavoritFragment)
    fun getBookmarkedFoods(): List<Food> {
        return foodList.filter { bookmarkedIds.contains(it.id) }
    }
}