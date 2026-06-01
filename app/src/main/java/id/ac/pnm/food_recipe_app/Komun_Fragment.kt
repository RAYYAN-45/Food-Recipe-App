package id.ac.pnm.food_recipe_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Komun_Fragment : Fragment() {

    private lateinit var recyclerKomunitas: RecyclerView
    private lateinit var editSearch: EditText
    private lateinit var txtEmpty: TextView
    private lateinit var fabTambah: FloatingActionButton
    private lateinit var tabSemua: TextView
    private lateinit var tabTersimpan: TextView

    private lateinit var adapterSemua: PostinganAdapter
    private lateinit var adapterTersimpan: PostinganAdapter

    // Tab yang sedang aktif: "semua" atau "tersimpan"
    private var tabAktif = "semua"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_komun, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerKomunitas = view.findViewById(R.id.recyclerKomunitas)
        editSearch        = view.findViewById(R.id.editSearchKomunitas)
        txtEmpty          = view.findViewById(R.id.txtEmptyKomunitas)
        fabTambah         = view.findViewById(R.id.fabTambahPostingan)
        tabSemua          = view.findViewById(R.id.tabSemua)
        tabTersimpan      = view.findViewById(R.id.tabTersimpan)

        recyclerKomunitas.layoutManager = LinearLayoutManager(requireContext())

        setupAdapters()
        tampilkanTabSemua()

        // ===== TAB SEMUA =====
        tabSemua.setOnClickListener {
            tabAktif = "semua"
            tampilkanTabSemua()
            editSearch.setText("")
        }

        // ===== TAB TERSIMPAN =====
        tabTersimpan.setOnClickListener {
            tabAktif = "tersimpan"
            tampilkanTabTersimpan()
            editSearch.setText("")
        }

        // ===== SEARCH REALTIME (hanya aktif di tab Semua) =====
        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (tabAktif == "semua") {
                    val hasil = PostinganDataSource.search(s.toString())
                    adapterSemua.updateData(hasil.toMutableList())
                    updateEmptyState(hasil.isEmpty())
                }
            }
        })

        // FAB → Apip yang handle
        fabTambah.setOnClickListener {
            Toast.makeText(requireContext(), "Tambah postingan (Apip)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAdapters() {
        // Adapter tab Semua
        adapterSemua = PostinganAdapter(
            list = PostinganDataSource.getAllPostingan().toMutableList(),
            onItemClick = { postingan ->
                // Anam yang handle detail postingan
                Toast.makeText(requireContext(), postingan.judulResep, Toast.LENGTH_SHORT).show()
            },
            onSimpanChanged = {
                // Refresh tab Tersimpan setiap kali ada perubahan simpan
                if (tabAktif == "tersimpan") {
                    adapterTersimpan.updateData(
                        PostinganDataSource.getSavedPostingan().toMutableList()
                    )
                    updateEmptyState(PostinganDataSource.getSavedPostingan().isEmpty())
                }
            }
        )

        // Adapter tab Tersimpan
        adapterTersimpan = PostinganAdapter(
            list = PostinganDataSource.getSavedPostingan().toMutableList(),
            onItemClick = { postingan ->
                Toast.makeText(requireContext(), postingan.judulResep, Toast.LENGTH_SHORT).show()
            },
            onSimpanChanged = {
                // Refresh list tersimpan saat ada yang di-unsave
                adapterTersimpan.updateData(
                    PostinganDataSource.getSavedPostingan().toMutableList()
                )
                updateEmptyState(PostinganDataSource.getSavedPostingan().isEmpty())
            }
        )
    }

    private fun tampilkanTabSemua() {
        // Update tampilan tab
        tabSemua.setTextColor(resources.getColor(R.color.brown_primary, null))
        tabSemua.setBackgroundResource(R.drawable.bg_tab_active)
        tabTersimpan.setTextColor(resources.getColor(android.R.color.darker_gray, null))
        tabTersimpan.setBackgroundResource(R.drawable.bg_tab_inactive)

        // Tampilkan semua postingan
        recyclerKomunitas.adapter = adapterSemua
        adapterSemua.updateData(PostinganDataSource.getAllPostingan().toMutableList())
        updateEmptyState(PostinganDataSource.getAllPostingan().isEmpty())

        // Sembunyikan search saat tab tersimpan
        editSearch.visibility = View.VISIBLE
    }

    private fun tampilkanTabTersimpan() {
        // Update tampilan tab
        tabTersimpan.setTextColor(resources.getColor(R.color.brown_primary, null))
        tabTersimpan.setBackgroundResource(R.drawable.bg_tab_active)
        tabSemua.setTextColor(resources.getColor(android.R.color.darker_gray, null))
        tabSemua.setBackgroundResource(R.drawable.bg_tab_inactive)

        // Tampilkan postingan tersimpan
        val saved = PostinganDataSource.getSavedPostingan()
        recyclerKomunitas.adapter = adapterTersimpan
        adapterTersimpan.updateData(saved.toMutableList())
        updateEmptyState(saved.isEmpty())

        // Sembunyikan search di tab tersimpan
        editSearch.visibility = View.GONE
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            txtEmpty.text = if (tabAktif == "tersimpan")
                "Belum ada postingan tersimpan.\nKlik ikon 🔖 untuk menyimpan!"
            else
                "Tidak ada postingan ditemukan"
            txtEmpty.visibility = View.VISIBLE
            recyclerKomunitas.visibility = View.GONE
        } else {
            txtEmpty.visibility = View.GONE
            recyclerKomunitas.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data sesuai tab aktif
        if (tabAktif == "tersimpan") {
            adapterTersimpan.updateData(PostinganDataSource.getSavedPostingan().toMutableList())
        } else {
            adapterSemua.updateData(PostinganDataSource.getAllPostingan().toMutableList())
        }
    }
}