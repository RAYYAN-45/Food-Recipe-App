package id.ac.pnm.food_recipe_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KomentarAdapter(
    // Langsung pakai referensi MutableList dari FoodDataSource
    // Sehingga kalau FoodDataSource diupdate, adapter otomatis punya data terbaru
    private val listKomentar: MutableList<Komentar>
) : RecyclerView.Adapter<KomentarAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNama: TextView  = itemView.findViewById(R.id.txtNamaKomentator)
        val txtIsi: TextView   = itemView.findViewById(R.id.txtIsiKomen)
        val txtWaktu: TextView = itemView.findViewById(R.id.txtWaktuKomen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_komentar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val komentar = listKomentar[position]
        holder.txtNama.text  = komentar.nama
        holder.txtIsi.text   = komentar.isi
        holder.txtWaktu.text = komentar.waktu
    }

    override fun getItemCount(): Int = listKomentar.size
}