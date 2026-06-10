package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class PostinganAdapter(
    private var list: MutableList<Postingan>,
    private var savedIds: Set<String>,
    private val onItemClick: (Postingan) -> Unit,
    private val onBookmarkClick: (Postingan) -> Unit,
    private val onDeleteClick: ((Postingan) -> Unit)? = null
) : RecyclerView.Adapter<PostinganAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfil: ImageView = itemView.findViewById(R.id.imgProfilPostingan)
        val txtNamaUser: TextView = itemView.findViewById(R.id.txtNamaUserPostingan)
        val txtJudul: TextView           = itemView.findViewById(R.id.txtJudulResepPostingan)
        val txtDeskripsi: TextView       = itemView.findViewById(R.id.txtDeskripsiPostingan)
        val txtJumlahKomen: TextView     = itemView.findViewById(R.id.txtJumlahKomenPostingan)
        val txtJumlahSimpan: TextView    = itemView.findViewById(R.id.txtJumlahSimpanPostingan)
        val txtJumlahShare: TextView     = itemView.findViewById(R.id.txtJumlahSharePostingan)
        val imgBookmark: ImageView       = itemView.findViewById(R.id.imgBookmarkPostingan)
        val layoutKomentar: LinearLayout = itemView.findViewById(R.id.layoutKomentarPostingan)
        val layoutSimpan: LinearLayout   = itemView.findViewById(R.id.layoutSimpanPostingan)
        val cardPostingan: View          = itemView.findViewById(R.id.cardPostingan)
        val btnHapus: ImageView = itemView.findViewById(R.id.btnHapusPostingan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_postingan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postingan = list[position]

        holder.imgProfil.setImageResource(postingan.fotoProfilUser)
        holder.txtNamaUser.text     = postingan.namaUser
        holder.txtJudul.text        = postingan.judulResep
        holder.txtDeskripsi.text    = postingan.deskripsi

        val postRef = com.google.firebase.database.FirebaseDatabase.getInstance()
            .getReference("postingan").child(postingan.postId)

        postRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener{
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                holder.txtJumlahKomen.text = snapshot.child("jumlahKomen").getValue(Int::class.java)?.toString() ?: "0"
                holder.txtJumlahShare.text = snapshot.child("jumlahShare").getValue(Int::class.java)?.toString() ?: "0"
                holder.txtJumlahSimpan.text = snapshot.child("jumlahSimpan").getValue(Int::class.java)?.toString() ?: "0"
            }

            override fun onCancelled(error: DatabaseError) {}

        })
        // Cek disimpan atau tidak
        val isSaved = savedIds.contains(postingan.postId)
        updateBookmarkIcon(holder.imgBookmark, isSaved)

        // Button Hapus Khusus milik sendiri
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (postingan.userId == currentUserId && onDeleteClick != null){
            holder.btnHapus.visibility = View.VISIBLE
            holder.btnHapus.setOnClickListener { onDeleteClick.invoke(postingan) }
        } else {
            holder.btnHapus.visibility = View.GONE
        }

        holder.cardPostingan.setOnClickListener { onItemClick(postingan) }

        //  Komentar
        holder.layoutKomentar.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailPostinganActivity::class.java)
            intent.putExtra("Extra_Postingan", postingan)
            holder.itemView.context.startActivity(intent)
        }

        //  Simpan
        holder.layoutSimpan.setOnClickListener {
            onBookmarkClick(postingan)
        }

    }

    override fun getItemCount(): Int = list.size

    // Fungsi untuk update data resep dan data bookmark sekaligus
    fun updateData(newList: List<Postingan>, newSavedIds: Set<String>) {
        list = newList.toMutableList()
        savedIds = newSavedIds
        notifyDataSetChanged()
    }

    private fun updateBookmarkIcon(imgBookmark: ImageView, isBookmarked: Boolean) {
        if (isBookmarked) {
            imgBookmark.setImageResource(R.drawable.bookmark_filled)
        } else {
            imgBookmark.setImageResource(R.drawable.bookmark)
        }
        imgBookmark.clearColorFilter()
    }
}