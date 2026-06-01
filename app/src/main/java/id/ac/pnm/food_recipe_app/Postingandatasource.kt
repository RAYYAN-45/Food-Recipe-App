package id.ac.pnm.food_recipe_app

object PostinganDataSource {

    private val semuaPostingan = mutableListOf(
        Postingan(
            id = 1,
            namaUser = "mak_dapur",
            fotoProfilUser = R.drawable.ic_star_border,
            judulResep = "Resep Nasi Goreng Jawa Selatan",
            deskripsi = "Nasi goreng seli jawa selatan mempunyai ciri khas yaitu berwarna merah",
            jumlahKomen = 123,
            jumlahSimpan = 123,
            jumlahShare = 123
        ),
        Postingan(
            id = 2,
            namaUser = "mu_sang_king",
            fotoProfilUser = R.drawable.ic_star_border,
            judulResep = "Resep opor kecap bango",
            deskripsi = "opor kecap bango adalah makanan khas dari kota Liverpool, Inggris.",
            jumlahKomen = 123,
            jumlahSimpan = 123,
            jumlahShare = 123
        )
    )

    // Set id postingan yang sudah disimpan/difavoritkan
    private val savedIds = mutableSetOf<Int>()

    fun getAllPostingan(): List<Postingan> = semuaPostingan.toList()

    // Ambil postingan yang sudah disimpan
    fun getSavedPostingan(): List<Postingan> {
        return semuaPostingan.filter { savedIds.contains(it.id) }
    }

    // Cek apakah postingan sudah disimpan
    fun isSaved(postinganId: Int): Boolean = savedIds.contains(postinganId)

    // Toggle simpan
    fun toggleSaved(postinganId: Int) {
        if (savedIds.contains(postinganId)) {
            savedIds.remove(postinganId)
        } else {
            savedIds.add(postinganId)
        }
    }

    // Search by nama user atau judul resep
    fun search(query: String): List<Postingan> {
        if (query.isBlank()) return semuaPostingan.toList()
        val q = query.lowercase()
        return semuaPostingan.filter {
            it.namaUser.lowercase().contains(q) ||
                    it.judulResep.lowercase().contains(q)
        }
    }

    // Dipanggil Apip saat user tambah postingan baru
    fun tambahPostingan(postingan: Postingan) {
        semuaPostingan.add(0, postingan)
    }
}