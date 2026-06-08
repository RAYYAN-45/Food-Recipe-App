package id.ac.pnm.food_recipe_app

import java.io.Serializable

data class Postingan(
    val id: Int,
    val namaUser: String,
    val fotoProfilUser: Int,
    val judulResep: String,
    val deskripsi: String,

//    val bahan: String,
//    val langkah: String,

    var jumlahKomen: Int = 0,
    var jumlahSimpan: Int = 0,
    var jumlahShare: Int = 0
) : Serializable