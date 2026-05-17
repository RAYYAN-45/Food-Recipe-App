package id.ac.pnm.food_recipe_app

object FoodDataSource {

    // Set untuk nyimpan ID makanan yang difavoritkan
    private val favoriteIds = mutableSetOf<Int>()

    // Map untuk nyimpan jumlah komentar, simpan, share per food id
    private val jumlahKomenMap  = mutableMapOf<Int, Int>()
    private val jumlahSimpanMap = mutableMapOf<Int, Int>()
    private val jumlahShareMap  = mutableMapOf<Int, Int>()

    // ===== TAMBAHAN: simpan list komentar per food id =====
    // Key = food.id, Value = list komentar untuk food tersebut
    private val komentarMap = mutableMapOf<Int, MutableList<Komentar>>()

    // Fungsi untuk dapat semua makanan
    fun getAllFoods(): List<Food> {
        return listOf(
            Food(
                id = 1,
                title = "Boeuf Bourguignon",
                desc = "Hidangan khas Perancis dengan daging sapi dimasak anggur merah...",
                image = R.drawable.item_1,
                ingredients = listOf(
                    "1 kg daging sapi potong dadu",
                    "200 ml anggur merah",
                    "2 wortel potong dadu",
                    "1 bawang bombay",
                    "3 siung bawang putih",
                    "2 sdm tepung terigu",
                    "Garam dan merica",
                    "Thyme dan bay leaf"
                ),
                steps = listOf(
                    "Lumuri daging dengan tepung, garam, dan merica",
                    "Tumis bawang bombay dan bawang putih hingga harum",
                    "Masukkan daging, masak hingga berubah warna",
                    "Tuang anggur merah, aduk rata",
                    "Tambahkan wortel dan bumbu aromatik",
                    "Masak dengan api kecil selama 2-3 jam hingga daging empuk",
                    "Sajikan hangat"
                )
            ),
            Food(
                id = 2,
                title = "Paella",
                desc = "Hidangan khas Spanyol dengan nasi saffron dicampur makanan laut...",
                image = R.drawable.paella,
                ingredients = listOf(
                    "300 gr beras",
                    "200 gr udang",
                    "150 gr kerang",
                    "100 gr cumi",
                    "1 paprika merah",
                    "1 tomat",
                    "Saffron",
                    "Kaldu ayam 500 ml",
                    "Minyak zaitun"
                ),
                steps = listOf(
                    "Panaskan minyak zaitun dalam wajan paella",
                    "Tumis paprika dan tomat hingga layu",
                    "Masukkan beras, aduk hingga tercampur",
                    "Tuang kaldu ayam dan saffron",
                    "Masak tanpa diaduk selama 15 menit",
                    "Tata udang, kerang, dan cumi di atas nasi",
                    "Masak hingga seafood matang dan nasi pulen",
                    "Sajikan dengan lemon"
                )
            ),
            Food(
                id = 3,
                title = "Souvlaki",
                desc = "Hidangan Yunani berupa potongan daging panggang...",
                image = R.drawable.souvlaki,
                ingredients = listOf(
                    "500 gr daging ayam/domba potong dadu",
                    "3 sdm minyak zaitun",
                    "2 siung bawang putih cincang",
                    "1 sdt oregano kering",
                    "Jus 1 lemon",
                    "Garam dan merica",
                    "Roti pita",
                    "Tzatziki sauce"
                ),
                steps = listOf(
                    "Campur daging dengan minyak zaitun, bawang putih, oregano, lemon, garam, merica",
                    "Diamkan minimal 1 jam di kulkas",
                    "Tusuk daging ke tusuk sate",
                    "Panggang di atas api/grill hingga matang kecoklatan",
                    "Hangatkan roti pita",
                    "Sajikan souvlaki dengan roti pita dan tzatziki sauce"
                )
            )
        )
    }

    // ===================== FAVORIT =====================

    fun toggleFavorite(foodId: Int) {
        if (favoriteIds.contains(foodId)) {
            favoriteIds.remove(foodId)
            jumlahSimpanMap[foodId] = maxOf(0, (jumlahSimpanMap[foodId] ?: 0) - 1)
        } else {
            favoriteIds.add(foodId)
            jumlahSimpanMap[foodId] = (jumlahSimpanMap[foodId] ?: 0) + 1
        }
    }

    fun isFavorite(foodId: Int): Boolean = favoriteIds.contains(foodId)

    fun getFavoriteFoods(): List<Food> {
        return getAllFoods().filter { food -> favoriteIds.contains(food.id) }
    }

    // ===================== JUMLAH =====================

    fun getJumlahKomen(foodId: Int): Int  = jumlahKomenMap[foodId] ?: 0
    fun getJumlahSimpan(foodId: Int): Int = jumlahSimpanMap[foodId] ?: 0
    fun getJumlahShare(foodId: Int): Int  = jumlahShareMap[foodId] ?: 0

    fun tambahShare(foodId: Int) {
        jumlahShareMap[foodId] = (jumlahShareMap[foodId] ?: 0) + 1
    }

    // ===================== KOMENTAR =====================

    // Ambil list komentar untuk food tertentu
    // Kalau belum ada, buat list kosong dulu
    fun getKomentar(foodId: Int): MutableList<Komentar> {
        if (!komentarMap.containsKey(foodId)) {
            komentarMap[foodId] = mutableListOf()
        }
        return komentarMap[foodId]!!
    }

    // Tambah komentar baru untuk food tertentu
    fun tambahKomentar(foodId: Int, komentar: Komentar) {
        if (!komentarMap.containsKey(foodId)) {
            komentarMap[foodId] = mutableListOf()
        }
        komentarMap[foodId]!!.add(0, komentar) // tambah di paling atas
        // update jumlah komentar
        jumlahKomenMap[foodId] = komentarMap[foodId]!!.size
    }

    // Ambil jumlah komentar aktual dari list
    fun getJumlahKomenAktual(foodId: Int): Int = komentarMap[foodId]?.size ?: 0
}