package id.ac.pnm.food_recipe_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(
    private var foodList: List<Food>,
    private val onItemClick: (Food) -> Unit,
    private val onFavoriteClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFood: ImageView = itemView.findViewById(R.id.imgFood)
        val imgStar: ImageView = itemView.findViewById(R.id.imgStar)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val txtDesc: TextView = itemView.findViewById(R.id.txtDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = foodList[position]

        holder.txtTitle.text = food.title
        holder.txtDesc.text = food.desc
        holder.imgFood.setImageResource(food.image)

        // 🔥 BAGIAN INI: cek apakah makanan sudah favorit
        if (FoodDataSource.isFavorite(food.id)) {
            holder.imgStar.setImageResource(R.drawable.ic_star_filled)
        } else {
            holder.imgStar.setImageResource(R.drawable.ic_star_border)
        }

        // Klik item pindah ke Detail
        holder.itemView.setOnClickListener {
            onItemClick(food)
        }

        // Klik bintang toggle favorit
        holder.imgStar.setOnClickListener {
            onFavoriteClick(food)
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    fun updateData(newList: List<Food>) {
        foodList = newList
        notifyDataSetChanged()
    }
}
