# 🍳 Recipe Box

**Recipe Box** adalah aplikasi Android yang menjadi asisten kuliner pribadi Anda. Temukan resep baru dari seluruh dunia, simpan resep favorit, buat rencana makan harian, dan nikmati pengalaman memasak yang lebih mudah dan menyenangkan.

Dibangun dengan arsitektur **MVVM + Clean Architecture**, menggunakan **Jetpack Compose** sebagai UI toolkit modern, dan didukung oleh **Spoonacular API** sebagai sumber data resep.

---

## 📸 Screenshot Aplikasi

| Onboarding | Beranda (Home) | Detail Resep | Resep Saya |
|:---:|:---:|:---:|:---:|
| <img src="screenshots/onboarding.jpeg" height="400" alt="Onboarding"/> | <img src="screenshots/home.jpeg" height="400" alt="Home Screen"/> | <img src="screenshots/detail.jpeg" height="400" alt="Recipe Detail"/> | <img src="screenshots/my_recipes.jpeg" height="400" alt="My Recipes"/> |

| Pencarian | Meal Plan | Tambah Resep | Pengaturan |
|:---:|:---:|:---:|:---:|
| <img src="screenshots/search.jpeg" height="400" alt="Search"/> | <img src="screenshots/meal_plan.jpeg" height="400" alt="Meal Plan"/> | <img src="screenshots/add_recipe.jpeg" height="400" alt="Add Recipe"/> | <img src="screenshots/settings.jpeg" height="400" alt="Settings"/> |

---

## ✨ Penjelasan Fitur

### 🏠 Home (Beranda)
- Menampilkan daftar **50 resep acak** dari Spoonacular API.
- Filter resep berdasarkan **kategori** (Main Course, Dessert, Side Dish, dll).
- Menampilkan **trivia makanan** acak yang menarik dengan dukungan terjemahan otomatis.
- Fallback ke **cache lokal** jika tidak ada koneksi internet.

### 🔍 Search (Pencarian)
- Pencarian resep secara real-time berdasarkan **nama resep**.
- Filter berdasarkan **kategori** yang tersedia.
- Menampilkan jumlah resep yang ditemukan.

### 📅 Meal Plan (Rencana Makan)
- Generate rencana makan harian otomatis (sarapan, makan siang, makan malam).
- Kustomisasi berdasarkan **target kalori** harian.
- Pilihan **tipe diet** (Vegetarian, Vegan, Gluten Free, dll).
- Menampilkan **total nutrisi harian** (kalori, protein, lemak, karbohidrat).

### 📖 My Recipes (Resep Saya)
- **Tab Buatan Sendiri**: Kelola resep personal yang Anda buat.
- **Tab Tersimpan**: Lihat resep dari API yang telah di-bookmark.
- Fitur **tambah, edit, dan hapus** resep personal.
- Upload foto resep dari **galeri** atau **kamera**.

### 📝 Detail Resep
- Informasi lengkap: deskripsi, bahan-bahan, langkah memasak.
- **Informasi gizi** (kalori, lemak, karbohidrat, protein).
- **Skor kesehatan** dan label diet (Vegetarian, Vegan, Gluten Free, Dairy Free).
- Tombol **bookmark** untuk menyimpan resep favorit.
- Tombol **share** untuk membagikan resep ke aplikasi lain.
- **Terjemahan otomatis** ke Bahasa Indonesia (menggunakan ML Kit on-device).

### ⚙️ Settings (Pengaturan)
- Toggle **Dark Mode** / Light Mode.
- Pergantian **bahasa** (English / Bahasa Indonesia) secara instan tanpa restart.
- Halaman **Tentang Aplikasi** dengan informasi teknologi yang digunakan.

### 🎬 Onboarding
- Layar sambutan pertama kali dengan 3 slide informatif.
- Hanya muncul sekali, kemudian disimpan di SharedPreferences.

---

## 🛠️ Cara Instalasi

### Prasyarat
- **Android Studio** Ladybug atau lebih baru
- **JDK 11** atau lebih baru
- **Android SDK** dengan API Level 24+ (minimum) dan 36 (target)
- **Koneksi internet** untuk mengambil resep dari API

### Langkah-langkah

1. **Clone repository**
   ```bash
   git clone https://github.com/daka69/RecipeBox.git
   cd recipe-box
   ```

2. **Dapatkan API Key dari Spoonacular**
   - Kunjungi [https://spoonacular.com/food-api](https://spoonacular.com/food-api)
   - Daftar akun gratis dan dapatkan API Key
   - API gratis memberikan **150 requests/hari**

3. **Konfigurasi API Key**

   Buka file `local.properties` di root project dan tambahkan:
   ```properties
   SPOONACULAR_API_KEYS=your_api_key_here
   ```

   > **Tips:** Untuk mendukung rotasi API Key otomatis saat kuota habis, Anda bisa menambahkan beberapa key yang dipisahkan koma:
   > ```properties
   > SPOONACULAR_API_KEYS=key1,key2,key3
   > ```

4. **Sync Gradle**

   Buka project di Android Studio dan klik **"Sync Now"** saat muncul notifikasi.

5. **Build project**
   ```bash
   ./gradlew assembleDebug
   ```

---

## ▶️ Cara Menjalankan Aplikasi

### Via Android Studio
1. Buka project di Android Studio
2. Pilih **device** atau **emulator** (minimum API 24 / Android 7.0)
3. Klik tombol **Run ▶** atau tekan `Shift + F10`

### Via Command Line
```bash
# Build dan install ke perangkat yang terhubung
./gradlew installDebug

# Atau build APK saja
./gradlew assembleDebug
# APK tersedia di: app/build/outputs/apk/debug/app-debug.apk
```

---

## 🌐 Informasi API yang Digunakan

Aplikasi ini menggunakan **[Spoonacular Food API](https://spoonacular.com/food-api)** sebagai sumber data utama.

| Endpoint | Method | Deskripsi |
|----------|--------|-----------|
| `/recipes/random` | GET | Mengambil resep acak (default: 50 resep) |
| `/recipes/{id}/information` | GET | Detail resep berdasarkan ID, termasuk informasi nutrisi |
| `/recipes/complexSearch` | GET | Pencarian resep berdasarkan query dan filter |
| `/food/trivia/random` | GET | Mengambil trivia/fakta menarik tentang makanan |
| `/mealplanner/generate` | GET | Generate rencana makan harian berdasarkan kalori dan diet |

### Autentikasi
- API Key ditambahkan otomatis ke setiap request melalui **OkHttp Interceptor**.
- Mendukung **rotasi API Key otomatis** jika satu key kehabisan kuota (HTTP 402).
- API Key disimpan di `local.properties` dan diakses melalui `BuildConfig` (tidak ter-commit ke Git).

### Base URL
```
https://api.spoonacular.com/
```

---

## 🏗️ Struktur Arsitektur Aplikasi

Aplikasi ini mengikuti pola **MVVM (Model-View-ViewModel)** dengan prinsip **Clean Architecture** yang membagi kode menjadi 3 layer utama:

```
┌─────────────────────────────────────────────────┐
│                 UI Layer (View)                  │
│  ┌───────────┐ ┌───────────┐ ┌───────────────┐  │
│  │  Screens  │ │Components │ │  Navigation   │  │
│  │ (Compose) │ │(Reusable) │ │   (NavHost)   │  │
│  └─────┬─────┘ └───────────┘ └───────────────┘  │
│        │                                         │
│  ┌─────▼──────────────────────────────────────┐  │
│  │         Presentation Layer                 │  │
│  │  ┌──────────┐ ┌──────────┐ ┌────────────┐  │  │
│  │  │  Home    │ │MyRecipes │ │  Detail    │  │  │
│  │  │ViewModel│ │ViewModel │ │ ViewModel  │  │  │
│  │  └────┬─────┘ └────┬─────┘ └─────┬──────┘  │  │
│  │       │            │             │          │  │
│  │  ┌────▼────┐ ┌─────▼───┐ ┌──────▼──────┐   │  │
│  │  │Settings │ │MealPlan │ │  UiState    │   │  │
│  │  │ViewModel│ │ViewModel│ │(Sealed Class│   │  │
│  │  └─────────┘ └─────────┘ └─────────────┘   │  │
│  └─────────────────────┬──────────────────────┘  │
│                        │                         │
├────────────────────────┼─────────────────────────┤
│               Domain Layer                       │
│  ┌─────────────┐ ┌────▼────────┐ ┌───────────┐  │
│  │   Models    │ │  Use Cases  │ │Repository │  │
│  │ (Recipe,    │ │ (10 total)  │ │(Interface)│  │
│  │ RecipeDetail│ │             │ │           │  │
│  │ MealPlan)   │ │             │ │           │  │
│  └─────────────┘ └─────────────┘ └───────────┘  │
│                                                  │
├──────────────────────────────────────────────────┤
│                 Data Layer                       │
│  ┌──────────┐  ┌──────────┐  ┌────────────────┐ │
│  │ Retrofit │  │   Room   │  │  Translation   │ │
│  │  (API)   │  │(Database)│  │  (ML Kit)      │ │
│  └──────────┘  └──────────┘  └────────────────┘ │
│  ┌──────────────────────────────────────────┐    │
│  │     RecipeRepositoryImpl                 │    │
│  │  (Single source of truth)                │    │
│  └──────────────────────────────────────────┘    │
└──────────────────────────────────────────────────┘
```

### Struktur Folder

```
app/src/main/java/com/example/recipebox/
├── MainActivity.kt                  # Entry point, inject ViewModels
├── RecipeBoxApplication.kt          # Hilt Application class
│
├── data/                            # DATA LAYER
│   ├── api/                         # API Service interface (Retrofit)
│   ├── remote/                      # Retrofit client & response DTOs
│   ├── local/                       # Room Entity, DAO, Converters, DataStore
│   ├── database/                    # Room Database & Migrations
│   ├── mapper/                      # Entity ↔ Domain mappers
│   ├── repository/                  # RecipeRepositoryImpl
│   └── translation/                 # ML Kit TranslationService
│
├── domain/                          # DOMAIN LAYER
│   ├── model/                       # Data classes (Recipe, RecipeDetail, MealPlan, dll)
│   ├── repository/                  # RecipeRepository interface
│   └── usecase/                     # 10 Use Cases (GetPublicRecipes, AddRecipe, dll)
│
├── presentation/                    # PRESENTATION LAYER
│   └── viewmodel/                   # 5 ViewModels + UiState sealed class
│
├── di/                              # Hilt Dependency Injection module
│
└── ui/                              # UI LAYER
    ├── screens/                     # 9 Composable screens
    ├── components/                  # Reusable UI components
    ├── navigation/                  # Screen sealed class (routes)
    ├── state/                       # UI state holders (IngredientInput, StepInput)
    └── theme/                       # Material 3 theme, colors, typography
```

### Teknologi yang Digunakan

| Teknologi | Versi | Fungsi |
|-----------|-------|--------|
| **Kotlin** | 2.0.21 | Bahasa pemrograman utama |
| **Jetpack Compose** | BOM latest | UI toolkit deklaratif modern |
| **Material 3** | Latest | Design system & komponen UI |
| **Hilt (Dagger)** | 2.51.1 | Dependency Injection |
| **Room** | 2.7.0 | Local database (SQLite) |
| **Retrofit** | 2.11.0 | HTTP client untuk REST API |
| **Gson** | 2.11.0 | JSON serialization/deserialization |
| **Coil** | 2.6.0 | Image loading & caching |
| **Navigation Compose** | 2.7.7 | In-app navigation |
| **DataStore** | 1.1.1 | Penyimpanan preferensi (dark mode, bahasa) |
| **ML Kit Translate** | 17.0.3 | Terjemahan on-device (EN → ID) |
| **Coroutines** | Latest | Asynchronous programming |

---

## ✅ Fitur Wajib

| # | Fitur Wajib | Status | Keterangan |
|---|-------------|--------|------------|
| 1 | **Menampilkan daftar resep** dari API | ✅ | 50 resep acak dari Spoonacular, dengan filter kategori |
| 2 | **Detail resep** (bahan, langkah, nutrisi) | ✅ | Informasi lengkap termasuk gizi, skor kesehatan, dan label diet |
| 3 | **Pencarian resep** | ✅ | Pencarian real-time berdasarkan nama dan kategori |
| 4 | **Simpan resep** ke lokal (bookmark) | ✅ | Toggle bookmark untuk resep API, tersimpan di Room database |
| 5 | **CRUD resep personal** | ✅ | Tambah, lihat, edit, dan hapus resep buatan sendiri |
| 6 | **Arsitektur MVVM** | ✅ | 5 ViewModel terpisah sesuai Single Responsibility Principle |
| 7 | **Penggunaan API eksternal** | ✅ | Spoonacular API dengan 5 endpoint berbeda |

---

## 🌟 Fitur Tambahan

| # | Fitur Tambahan | Keterangan |
|---|---------------|------------|
| 1 | **Meal Plan Generator** | Generate rencana makan harian otomatis berdasarkan target kalori dan tipe diet |
| 2 | **Terjemahan Otomatis (ML Kit)** | Terjemahan on-device EN→ID untuk detail resep dan trivia, tanpa perlu koneksi internet setelah model diunduh |
| 3 | **Dukungan Multi-Bahasa** | UI mendukung Bahasa Indonesia dan English, dapat diganti secara instan dari Settings |
| 4 | **Dark Mode** | Toggle dark mode yang tersimpan persisten di DataStore |
| 5 | **Onboarding Screen** | Layar sambutan dengan 3 slide informatif untuk pengguna baru |
| 6 | **Offline Caching** | Resep API di-cache ke Room database, tetap bisa diakses tanpa internet |
| 7 | **Share Resep** | Membagikan resep ke aplikasi lain melalui Intent Share |
| 8 | **Deep Link** | Navigasi langsung ke detail resep via URI `recipebox://detail/{recipeId}` |
| 9 | **Upload Foto Resep** | Pilih foto dari galeri atau ambil langsung dari kamera untuk resep personal |
| 10 | **API Key Rotation** | Rotasi otomatis ke API key cadangan saat kuota key utama habis (HTTP 402) |
| 11 | **Food Trivia** | Fakta menarik tentang makanan ditampilkan di beranda, dengan terjemahan otomatis |
| 12 | **Clean Architecture** | Pemisahan layer (Data, Domain, Presentation, UI) dengan Dependency Injection via Hilt |

---

## 📄 Lisensi

Proyek ini dibuat untuk keperluan akademis.

---

<p align="center">
  Dibuat dengan ❤️ menggunakan <b>Kotlin</b> dan <b>Jetpack Compose</b>
</p>
