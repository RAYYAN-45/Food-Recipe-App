package id.ac.pnm.food_recipe_app

import java.io.Serializable

data class Postingan(
    val id: Int,
    val namaUser: String,       // nama akun yang posting
    val fotoProfilUser: Int,    // drawable foto profil
    val judulResep: String,     // judul resep yang diposting
    val deskripsi: String,      // deskripsi singkat
    var jumlahKomen: Int = 0,
    var jumlahSimpan: Int = 0,
    var jumlahShare: Int = 0
) : Serializable