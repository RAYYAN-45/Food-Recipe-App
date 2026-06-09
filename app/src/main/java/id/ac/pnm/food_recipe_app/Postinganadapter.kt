package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostinganAdapter(
    private var list: MutableList<Postingan>,
    private var savedIds: Set<String>,
    private val onItemClick: (Postingan) -> Unit,
    private val onBookmarkClick: (Postingan) -> Unit
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
        val layoutShare: LinearLayout = itemView.findViewById(R.id.layoutSharePostingan)
        val cardPostingan: View          = itemView.findViewById(R.id.cardPostingan)
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
        holder.txtJumlahKomen.text  = postingan.jumlahKomen.toString()
        holder.txtJumlahSimpan.text = postingan.jumlahSimpan.toString()
        holder.txtJumlahShare.text  = postingan.jumlahShare.toString()

        // Cek disimpan atau tidak
        val isSaved = savedIds.contains(postingan.postId)
        updateBookmarkIcon(holder.imgBookmark, isSaved)

        holder.cardPostingan.setOnClickListener { onItemClick(postingan) }

        //  Komentar
        holder.layoutKomentar.setOnClickListener {
            postingan.jumlahKomen++
            holder.txtJumlahKomen.text = postingan.jumlahKomen.toString()
            val intent = Intent(holder.itemView.context, DetailPostinganActivity::class.java)
            intent.putExtra("Extra_Postingan", postingan)
            holder.itemView.context.startActivity(intent)
        }

        //  Simpan
        holder.layoutSimpan.setOnClickListener {
            onBookmarkClick(postingan)
        }

        //  Share
        holder.layoutShare.setOnClickListener {
            postingan.jumlahShare++
            holder.txtJumlahShare.text = postingan.jumlahShare.toString()

            val shareText = "${postingan.namaUser}: ${postingan.judulResep}\n\n${postingan.deskripsi}"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            holder.itemView.context.startActivity(Intent.createChooser(shareIntent, "Bagikan via..."))
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