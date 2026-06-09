# MusikApp — Aplikasi Manajemen Musik

Aplikasi desktop berbasis **JavaFX** untuk mengelola koleksi lagu, playlist, dan data musik secara lokal. Dibangun sebagai Mini Project Praktikum Rekayasa Perangkat Lunak (RPL).

## Deskripsi

MusikApp adalah aplikasi manajemen musik berbasis GUI yang memungkinkan pengguna untuk mengelola koleksi lagu pribadi, membuat playlist, menandai lagu favorit, serta mengekspor/impor data dalam format CSV. Aplikasi ini menggunakan database SQLite untuk penyimpanan data yang persisten.

## Fitur Utama

- Autentikasi — Login dan registrasi akun pengguna
- Manajemen Lagu — Tambah, edit, hapus, dan cari lagu
- Playlist — Buat dan kelola playlist (termasuk mode public/private)
- Favorit — Tandai lagu sebagai favorit
- Filter & Pencarian — Cari berdasarkan keyword, artis, atau genre
- Statistik Genre — Visualisasi distribusi genre lagu
- Export CSV — Ekspor daftar lagu ke file CSV
- Import CSV — Impor lagu dari file CSV
- Keyboard Shortcut — Navigasi cepat menggunakan shortcut

**Design Pattern yang diterapkan:**
- MVC Pattern — Pemisahan antara Model, View (FXML), dan Controller
- Singleton Pattern — MusicManager sebagai pusat koordinasi
- DAO Pattern — Abstraksi akses database via interface DAO
- Encapsulation — Private fields dengan getter/setter di semua model

## Cara Menjalankan

### 1. Clone Repository

```bash
git clone https://github.com/username/Mini-Project-PrakRPL.git
cd Mini-Project-PrakRPL
```

### 2. Jalankan dengan Maven

```bash
mvn javafx:run
```

### 3. Build JAR (opsional)

```bash
mvn clean package
```

## Cara Penggunaan

1. Jalankan aplikasi — akan muncul halaman **Login**
2. Jika belum punya akun, klik **Register** untuk membuat akun baru
3. Setelah login, kamu akan masuk ke **Dashboard Utama**
4. Gunakan tab/menu untuk:
   - Menambah lagu baru via tombol **Tambah**
   - Mencari lagu via kolom **Search**
   - Membuat playlist baru via panel **Playlist**
   - Mengekspor data via menu **Export CSV**
5. Saat menutup aplikasi, akan muncul konfirmasi untuk **menyimpan data**

## 🗄️ Database

Aplikasi menggunakan **SQLite** dengan file database lokal bernama `musikapp.db` yang otomatis dibuat di direktori project saat pertama kali dijalankan.

## 👥 Anggota Kelompok

| Nama | NIM |
|---|---|
| Klemens Aurel Adyatma | 71241076 |
| Audris Kasula | 71241105 |
| Benteng Gading Aribowo | 71241143 |
| Richard Vanuella | 71241159 |
