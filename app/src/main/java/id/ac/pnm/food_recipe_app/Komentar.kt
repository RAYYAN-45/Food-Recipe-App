package id.ac.pnm.food_recipe_app

// Model data untuk satu komentar
data class Komentar(
    val nama: String,   // nama pengguna
    val isi: String,    // isi komentar
    val waktu: String   // waktu komentar (contoh: "Baru saja", "2 menit lalu")
)