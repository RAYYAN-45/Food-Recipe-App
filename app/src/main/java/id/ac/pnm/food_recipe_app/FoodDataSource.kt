package id.ac.pnm.food_recipe_app

// Tempat nyimpan semua data makanan
object FoodDataSource {

    // Set untuk nyimpan ID makanan yang difavoritkan
    // Contoh: jika Paella (id=2) difavoritkan, maka favoriteIds = [2]
    private val favoriteIds = mutableSetOf<Int>()

    // Fungsi untuk dapat semua makanan
    fun getAllFoods(): List<Food> {
        return listOf(
            // Makanan 1: Boeuf Bourguignon
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
            // Makanan 2: Paella
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
            // Makanan 3: Souvlaki
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

    // Fungsi untuk tambah/hapus favorit
    // Kalau sudah favorit -> hapus
    // Kalau belum favorit -> tambah
    fun toggleFavorite(foodId: Int) {
        if (favoriteIds.contains(foodId)) {
            // Kalau sudah ada, hapus dari favorit
            favoriteIds.remove(foodId)
        } else {
            // Kalau belum ada, tambahkan ke favorit
            favoriteIds.add(foodId)
        }
    }

    // Fungsi cek apakah makanan ini sudah difavoritkan
    fun isFavorite(foodId: Int): Boolean {
        return favoriteIds.contains(foodId)
    }

    // Fungsi untuk dapat makanan yang difavoritkan aja
    fun getFavoriteFoods(): List<Food> {
        // Ambil semua makanan, filter yang ID nya ada di favoriteIds
        return getAllFoods().filter { food ->
            favoriteIds.contains(food.id)
        }
    }
}