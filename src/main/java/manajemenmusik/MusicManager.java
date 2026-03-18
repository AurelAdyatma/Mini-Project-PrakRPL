package manajemenmusik;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MusicManager {
    private final ObservableList<Song> daftarLagu;

    public MusicManager() {
        daftarLagu = FXCollections.observableArrayList();
    }

    public ObservableList<Song> getDaftarLagu() {
        return daftarLagu;
    }

    public void tambahLagu(Song song) {
        daftarLagu.add(song);
    }

    public void hapusLagu(Song song) {
        daftarLagu.remove(song);
    }

    public ObservableList<Song> cariLagu(String kataKunci) {
        ObservableList<Song> hasil = FXCollections.observableArrayList();

        if (kataKunci == null || kataKunci.trim().isEmpty()) {
            hasil.addAll(daftarLagu);
            return hasil;
        }

        String keyword = kataKunci.toLowerCase();

        for (Song song : daftarLagu) {
            if (song.getJudul().toLowerCase().contains(keyword)
                    || song.getArtis().toLowerCase().contains(keyword)
                    || song.getGenre().toLowerCase().contains(keyword)) {
                hasil.add(song);
            }
        }

        return hasil;
    }
}