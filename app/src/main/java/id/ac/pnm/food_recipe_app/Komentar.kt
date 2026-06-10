package id.ac.pnm.food_recipe_app

// Model data untuk satu komentar
data class Komentar(
    val komentarId: String = "",
    val userId: String = "",
    val namaUser: String = "",
    val isiKomentar: String = "",
    val timestamp: Long = 0L
)