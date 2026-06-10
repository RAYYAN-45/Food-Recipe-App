package id.ac.pnm.food_recipe_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile_Fragment : Fragment() {

    private lateinit var recyclerPostingan: RecyclerView
    private lateinit var adapter: PostinganAdapter
    private val listPostinganSaya = mutableListOf<Postingan>()
    private var savedIds = setOf<String>()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //mengambil username dari MainActivity
        val username = FirebaseAuth.getInstance().currentUser
        val TextViewUsername = view.findViewById<TextView>(R.id.TextViewUsername)

        if(username != null){
            val user = username.displayName

            TextViewUsername.text = user ?: "Pengguna Baru"
        }

        //button logout
        val btnLogout = view.findViewById<Button>(R.id.BtnLogout)
        btnLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            Toast.makeText(requireContext(), "Telah Log Out", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags  = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        recyclerPostingan = view.findViewById(R.id.recyclerPostingan)
        recyclerPostingan.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        setupAdapter()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            tarikDataPostinganSaya(currentUser.uid)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // atur adapter
    private fun setupAdapter() {
        adapter = PostinganAdapter(
            list = listPostinganSaya,
            savedIds = savedIds,
            onItemClick = { postingan ->
                val intent = Intent(requireContext(), DetailPostinganActivity::class.java)
                intent.putExtra("Extra_Postingan", postingan)
                startActivity(intent)
            },
            onBookmarkClick = { postingan ->
                Toast.makeText(requireContext(), "Fitur simpan", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { postingan ->
                tampilkanDialogHapus(postingan)
            }
        )
        recyclerPostingan.adapter = adapter
    }

    // tarik resep khusus milik user ini
    private fun tarikDataPostinganSaya(userId: String) {
        val dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("postingan")
        val query = dbRef.orderByChild("userId").equalTo(userId)

        query.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                listPostinganSaya.clear()
                for (child in snapshot.children) {
                    val postingan = child.getValue(Postingan::class.java)
                    if (postingan != null) {
                        listPostinganSaya.add(0, postingan) // Urutan terbaru di atas
                    }
                }
                adapter.updateData(listPostinganSaya, savedIds)
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }

    //  pop-up konfirmasi hapus
    private fun tampilkanDialogHapus(postingan: Postingan) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Hapus Resep")
            .setMessage("Yakin ingin menghapus resep '${postingan.judulResep}' secara permanen?")
            .setPositiveButton("Hapus") { _, _ ->
                val dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("postingan").child(postingan.postId)
                dbRef.removeValue().addOnSuccessListener {
                    Toast.makeText(requireContext(), "Resep berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}