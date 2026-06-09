package id.ac.pnm.food_recipe_app

import java.io.Serializable

data class Postingan(
    var postId: String = "",
    var userId: String = "",
    var namaUser: String = "",
    var judulResep: String = "",
    var deskripsi: String = "",
    var bahan: String = "",
    var langkah: String = "",
    var timestamp: Long = 0L,

    var fotoProfilUser: Int = 0,
    var jumlahKomen: Int = 0,
    var jumlahSimpan: Int = 0,
    var jumlahShare: Int = 0,
) : Serializable