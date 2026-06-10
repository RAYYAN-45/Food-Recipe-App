package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class FoodAdapter(
    private var foodList: List<Food>,
    private val onItemClick: (Food) -> Unit,
    private val onKomentarClick: (Food) -> Unit,
    private val onSimpanClick: (Food, Boolean) -> Unit,
    private var savedIds: Set<Int>
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
        val isSaved = savedIds.contains(food.id)

        // Tampilkan data resep
        holder.txtTitle.text = food.title
        holder.txtDesc.text  = food.desc
        holder.imgFood.setImageResource(food.image)

        // Angka komentar & share dari Firebase
        val dbKomentarRef = com.google.firebase.database.FirebaseDatabase.getInstance()
            .getReference("komentar").child(food.id.toString())

        dbKomentarRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                holder.txtJumlahKomen.text = snapshot.childrenCount.toString()
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
        val metaRef = com.google.firebase.database.FirebaseDatabase.getInstance()
            .getReference("meta_resep_lokal").child(food.id.toString())

        metaRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val simpan = snapshot.child("jumlahSimpan").getValue(Int::class.java)?:0
                val share = snapshot.child("jumlahShare").getValue(Int::class.java)?:0
                holder.txtJumlahSimpan.text = simpan.toString()
                holder.txtJumlahShare.text = share.toString()
            }

            override fun onCancelled(error: DatabaseError) {}

        })


        // Angka simpan & status bookmark dari field isFavorite Room
        updateBookmarkIcon(holder.imgBookmark, isSaved)

        // Klik card → buka Detail
        holder.itemView.setOnClickListener {
            onItemClick(food)
        }

        // LISTENER 1: Komentar → update angka langsung + buka detail
        holder.layoutKomentar.setOnClickListener {
            onKomentarClick(food)
        }

        // LISTENER 2: Simpan → toggle bookmark + update angka
        // Status simpan dihandle Room lewat callback onSimpanClick
        holder.layoutSimpan.setOnClickListener {
            updateBookmarkIcon(holder.imgBookmark, !isSaved)
            onSimpanClick(food, isSaved)
        }
    }

    override fun getItemCount(): Int = foodList.size

    fun updateData(newList: List<Food>, newSavedIds: Set<Int>) {
        foodList = newList
        savedIds = newSavedIds
        notifyDataSetChanged()
    }

    // Belum disimpan → bookmark outline
    // Sudah disimpan → bookmark_filled hitam penuh
    private fun updateBookmarkIcon(imgBookmark: ImageView, isBookmarked: Boolean) {
        if (isBookmarked) {
            imgBookmark.setImageResource(R.drawable.bookmark_filled)
        } else {
            imgBookmark.setImageResource(R.drawable.bookmark)
        }
        imgBookmark.clearColorFilter()
    }
}