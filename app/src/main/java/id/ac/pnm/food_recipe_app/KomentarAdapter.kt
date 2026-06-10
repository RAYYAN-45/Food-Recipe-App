package id.ac.pnm.food_recipe_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KomentarAdapter(
    // Langsung pakai referensi MutableList dari FoodDataSource
    // Sehingga kalau FoodDataSource diupdate, adapter otomatis punya data terbaru
    private val listKomentar: MutableList<Komentar>
) : RecyclerView.Adapter<KomentarAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNama: TextView = itemView.findViewById(R.id.txtNamaKomentator)
        val txtWaktu: TextView = itemView.findViewById(R.id.txtWaktuKomen)
        val txtIsi: TextView = itemView.findViewById(R.id.txtIsiKomen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_komentar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val komentar = listKomentar[position]

        holder.txtNama.text = komentar.namaUser
        holder.txtIsi.text = komentar.isiKomentar

        // Ubah angka timestamp menjadi format tanggal/jam yang mudah dibaca (Contoh: 15 Aug, 14:30)
        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        val date = Date(komentar.timestamp)
        holder.txtWaktu.text = sdf.format(date)

    }

    override fun getItemCount(): Int = listKomentar.size

    fun updateData(newList: List<Komentar>) {
        listKomentar.clear()
        listKomentar.addAll(newList)
        notifyDataSetChanged()
    }
}