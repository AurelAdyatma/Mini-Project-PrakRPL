package manajemenmusik.dao;

import javafx.collections.ObservableList;
import manajemenmusik.model.Playlist;
import manajemenmusik.model.Song;

public interface PlaylistDAO {
    ObservableList<Playlist> getAllPlaylists();
    void tambahPlaylist(Playlist playlist);
    void hapusPlaylist(Playlist playlist);
    void hapusLaguDariSemuaPlaylist(Song song);
    void simpanData();
    void muatData(ObservableList<Song> semuaLagu, int currentUserId);
    void togglePublic(Playlist playlist);
}
