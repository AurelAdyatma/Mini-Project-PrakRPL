package manajemenmusik;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MusicManager {
    private final ObservableList<Song>     daftarLagu     = FXCollections.observableArrayList();
    private final ObservableList<Playlist> daftarPlaylist = FXCollections.observableArrayList();

    // ---- Getters ----
    public ObservableList<Song>     getDaftarLagu()     { return daftarLagu; }
    public ObservableList<Playlist> getDaftarPlaylist() { return daftarPlaylist; }

    // ---- CRUD Lagu ----

    /** Tambah lagu; returns false jika ID sudah ada. */
    public boolean tambahLagu(Song song) {
        for (Song s : daftarLagu) {
            if (s.getId().equalsIgnoreCase(song.getId())) return false;
        }
        daftarLagu.add(song);
        return true;
    }

    /** Hapus lagu dari semua playlist juga. */
    public void hapusLagu(Song song) {
        daftarLagu.remove(song);
        daftarPlaylist.forEach(pl -> pl.getLagu().remove(song));
    }

    /** Edit properti lagu secara in-place (JavaFX properties auto-refresh tabel). */
    public void editLagu(Song orig, String id, String judul, String artis,
                         String genre, int durasi, int tahun) {
        orig.setId(id);
        orig.setJudul(judul);
        orig.setArtis(artis);
        orig.setGenre(genre);
        orig.setDurasi(durasi);
        orig.setTahun(tahun);
    }

    // ---- Pencarian & Filter ----

    public ObservableList<Song> cariLagu(String kw) {
        if (kw == null || kw.isBlank()) return daftarLagu;
        String k = kw.toLowerCase();
        ObservableList<Song> hasil = FXCollections.observableArrayList();
        for (Song s : daftarLagu) {
            if (s.getId().toLowerCase().contains(k)
                    || s.getJudul().toLowerCase().contains(k)
                    || s.getArtis().toLowerCase().contains(k)
                    || s.getGenre().toLowerCase().contains(k)) {
                hasil.add(s);
            }
        }
        return hasil;
    }

    public ObservableList<Song> filterGenre(String genre) {
        if (genre == null || genre.equals("Semua Genre")) return daftarLagu;
        ObservableList<Song> hasil = FXCollections.observableArrayList();
        daftarLagu.stream()
                .filter(s -> s.getGenre().equalsIgnoreCase(genre))
                .forEach(hasil::add);
        return hasil;
    }

    /** Filter kombinasi keyword + genre. */
    public ObservableList<Song> filter(String keyword, String genre) {
        String kw = (keyword == null) ? "" : keyword.toLowerCase();
        boolean filterGenre = genre != null && !genre.equals("Semua Genre");
        ObservableList<Song> hasil = FXCollections.observableArrayList();
        for (Song s : daftarLagu) {
            boolean matchGenre = !filterGenre || s.getGenre().equalsIgnoreCase(genre);
            boolean matchKw = kw.isBlank()
                    || s.getId().toLowerCase().contains(kw)
                    || s.getJudul().toLowerCase().contains(kw)
                    || s.getArtis().toLowerCase().contains(kw)
                    || s.getGenre().toLowerCase().contains(kw);
            if (matchGenre && matchKw) hasil.add(s);
        }
        return hasil;
    }

    // ---- Favorit ----

    public ObservableList<Song> getDaftarFavorit() {
        ObservableList<Song> hasil = FXCollections.observableArrayList();
        daftarLagu.stream().filter(Song::isFavorit).forEach(hasil::add);
        return hasil;
    }

    // ---- Statistik ----

    public Set<String> getGenreUnik() {
        return daftarLagu.stream().map(Song::getGenre)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public Set<String> getArtisUnik() {
        return daftarLagu.stream().map(Song::getArtis)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public Map<String, Long> getStatistikGenre() {
        return daftarLagu.stream()
                .collect(Collectors.groupingBy(Song::getGenre, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    // ---- Playlist ----

    public void tambahPlaylist(Playlist pl) { daftarPlaylist.add(pl); }
    public void hapusPlaylist(Playlist pl)  { daftarPlaylist.remove(pl); }

    // ---- Import / Export CSV ----

    public void exportCSV(File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            pw.println("ID,Judul,Artis,Genre,Durasi,Tahun,Favorit");
            daftarLagu.forEach(s -> pw.println(s.toCSV()));
        }
    }

    public int importCSV(File file) throws IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = parseCsvLine(line);
                if (p.length >= 6) {
                    try {
                        Song s = new Song(p[0], p[1], p[2], p[3],
                                Integer.parseInt(p[4]), Integer.parseInt(p[5]));
                        if (p.length >= 7) s.setFavorit(Boolean.parseBoolean(p[6]));
                        if (tambahLagu(s)) count++;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return count;
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString().trim());
        return result.toArray(new String[0]);
    }
}