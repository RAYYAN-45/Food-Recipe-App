package id.ac.pnm.food_recipe_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BahanLangkahAdapter (
    private val items: List<String>,
    private val isLangkah: Boolean = false
) : RecyclerView.Adapter<BahanLangkahAdapter.TextViewHolder>() {

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return TextViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        val teks = items[position]

        if (isLangkah) {
            holder.textView.text = "${position + 1}. $teks"
        } else {
            holder.textView.text = "• $teks"
        }

        holder.textView.setTextColor(android.graphics.Color.BLACK)
    }

    override fun getItemCount(): Int = items.size
}