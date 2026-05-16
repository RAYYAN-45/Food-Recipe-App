package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(
    private var foodList: List<Food>,
    private val onItemClick: (Food) -> Unit,
    private val onKomentarClick: (Food) -> Unit,
    private val onSimpanClick: (Food) -> Unit,
    private val onShareClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = foodList[position]

        // Tampilkan data
        holder.txtTitle.text = food.title
        holder.txtDesc.text  = food.desc
        holder.imgFood.setImageResource(food.image)

        // Tampilkan angka dari FoodDataSource (global/shared)
        holder.txtJumlahKomen.text  = FoodDataSource.getJumlahKomen(food.id).toString()
        holder.txtJumlahSimpan.text = FoodDataSource.getJumlahSimpan(food.id).toString()
        holder.txtJumlahShare.text  = FoodDataSource.getJumlahShare(food.id).toString()

        // Tampilkan status bookmark dari FoodDataSource (sinkron semua fragment)
        updateBookmarkIcon(holder.imgBookmark, FoodDataSource.isFavorite(food.id))

        // Klik card → buka Detail
        holder.itemView.setOnClickListener {
            onItemClick(food)
        }

        // LISTENER 1: Komentar → tambah angka + callback
        holder.layoutKomentar.setOnClickListener {
            FoodDataSource.tambahKomen(food.id)
            holder.txtJumlahKomen.text = FoodDataSource.getJumlahKomen(food.id).toString()
            onKomentarClick(food)
        }

        // LISTENER 2: Simpan → toggle bookmark + update angka + ikon
        holder.layoutSimpan.setOnClickListener {
            FoodDataSource.toggleFavorite(food.id) // toggle sekaligus update jumlahSimpan
            val isNowBookmarked = FoodDataSource.isFavorite(food.id)
            updateBookmarkIcon(holder.imgBookmark, isNowBookmarked)
            holder.txtJumlahSimpan.text = FoodDataSource.getJumlahSimpan(food.id).toString()
            onSimpanClick(food)
        }

        // LISTENER 3: Share → tambah angka + callback
        holder.layoutShare.setOnClickListener {
            FoodDataSource.tambahShare(food.id)
            holder.txtJumlahShare.text = FoodDataSource.getJumlahShare(food.id).toString()
            onShareClick(food)
        }
    }

    override fun getItemCount(): Int = foodList.size

    fun updateData(newList: List<Food>) {
        foodList = newList
        notifyDataSetChanged()
    }

    // Belum disimpan → @drawable/bookmark (outline, warna asli)
    // Sudah disimpan → @drawable/bookmark_filled (solid hitam penuh)
    private fun updateBookmarkIcon(imgBookmark: ImageView, isBookmarked: Boolean) {
        if (isBookmarked) {
            imgBookmark.setImageResource(R.drawable.bookmark_filled)
        } else {
            imgBookmark.setImageResource(R.drawable.bookmark)
        }
        imgBookmark.clearColorFilter()
    }

    fun getBookmarkedFoods(): List<Food> {
        return foodList.filter { FoodDataSource.isFavorite(it.id) }
    }
}