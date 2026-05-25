package manajemenmusik.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import manajemenmusik.model.Song;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SongDAOImpl — implementasi konkret dari SongDAO (DAO Pattern + Polymorphism).
 * Mengelola penyimpanan data lagu dalam ObservableList dan file CSV.
 */
public class SongDAOImpl implements SongDAO {

    private final ObservableList<Song> daftarLagu = FXCollections.observableArrayList();

    // ---- CRUD ----

    @Override
    public boolean tambah(Song song) {
        for (Song s : daftarLagu) {
            if (s.getId().equalsIgnoreCase(song.getId())) return false;
        }
        daftarLagu.add(song);
        return true;
    }

    @Override
    public void hapus(Song song) {
        daftarLagu.remove(song);
    }

    @Override
    public void edit(Song orig, String id, String judul, String artis,
                     String genre, int durasi, int tahun) {
        orig.setId(id);
        orig.setJudul(judul);
        orig.setArtis(artis);
        orig.setGenre(genre);
        orig.setDurasi(durasi);
        orig.setTahun(tahun);
    }

    // ---- Query ----

    @Override
    public ObservableList<Song> getAll() {
        return daftarLagu;
    }

    @Override
    public ObservableList<Song> getFavorit() {
        ObservableList<Song> hasil = FXCollections.observableArrayList();
        daftarLagu.stream().filter(Song::isFavorit).forEach(hasil::add);
        return hasil;
    }

    @Override
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

    // ---- Statistik ----

    @Override
    public Set<String> getGenreUnik() {
        return daftarLagu.stream().map(Song::getGenre)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public Set<String> getArtisUnik() {
        return daftarLagu.stream().map(Song::getArtis)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public Map<String, Long> getStatistikGenre() {
        return daftarLagu.stream()
                .collect(Collectors.groupingBy(Song::getGenre, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    // ---- CSV Import / Export ----

    @Override
    public void exportCSV(File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            pw.println("ID,Judul,Artis,Genre,Durasi,Tahun,Favorit");
            daftarLagu.forEach(s -> pw.println(s.toCSV()));
        }
    }

    @Override
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
                        if (tambah(s)) count++;
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
