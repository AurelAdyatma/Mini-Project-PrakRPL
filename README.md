MusikApp — Aplikasi Manajemen Musik:
Aplikasi desktop berbasis JavaFX untuk mengelola koleksi lagu, playlist, dan data musik secara lokal. Dibangun sebagai Mini Project Praktikum Rekayasa Perangkat Lunak (RPL).

Deskripsi:
MusikApp adalah aplikasi manajemen musik berbasis GUI yang memungkinkan pengguna untuk mengelola koleksi lagu pribadi, membuat playlist, menandai lagu favorit, serta mengekspor/impor data dalam format CSV. Aplikasi ini menggunakan database SQLite untuk penyimpanan data yang persisten.

Fitur Utama:
󠁯•󠁏󠁏 🔐 Autentikasi — Login dan registrasi akun pengguna
󠁯•󠁏󠁏 🎵 Manajemen Lagu — Tambah, edit, hapus, dan cari lagu
󠁯•󠁏󠁏 📂 Playlist — Buat dan kelola playlist (termasuk mode public/private)
󠁯•󠁏󠁏 ⭐ Favorit — Tandai lagu sebagai favorit
󠁯•󠁏󠁏 🔍 Filter & Pencarian — Cari berdasarkan keyword, artis, atau genre
󠁯•󠁏󠁏 📊 Statistik Genre — Visualisasi distribusi genre lagu
󠁯•󠁏󠁏 📤 Export CSV — Ekspor daftar lagu ke file CSV
󠁯•󠁏󠁏 📥 Import CSV — Impor lagu dari file CSV
󠁯•󠁏󠁏 ⌨️ Keyboard Shortcut — Navigasi cepat menggunakan shortcut

Design Pattern yang diterapkan:
󠁯•󠁏󠁏 MVC Pattern — Pemisahan antara Model, View (FXML), dan Controller
󠁯•󠁏󠁏 Singleton Pattern — MusicManager sebagai pusat koordinasi
󠁯•󠁏󠁏 DAO Pattern — Abstraksi akses database via interface DAO
󠁯•󠁏󠁏 Encapsulation — Private fields dengan getter/setter di semua model
