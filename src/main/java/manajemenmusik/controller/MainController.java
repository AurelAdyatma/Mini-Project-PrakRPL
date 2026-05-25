package manajemenmusik.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import manajemenmusik.model.Playlist;
import manajemenmusik.model.Song;
import manajemenmusik.model.User;
import manajemenmusik.service.MusicManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * MainController — MVC Controller layer untuk MainView.fxml.
 * Mengelola semua event dan data binding.
 */
public class MainController {

    private final MusicManager manager = MusicManager.getInstance();
    private Runnable onLogout;
    private Song selectedSong = null;
    private static final String DB_FILE = "music_database.csv";

    private static final String ACTIVE_STYLE = 
        "-fx-background-color: #FFFFFF; -fx-background-radius: 6; " +
        "-fx-text-fill: #111827; -fx-font-size: 13px; -fx-font-weight: bold; " +
        "-fx-cursor: hand; -fx-padding: 10 14; " +
        "-fx-border-color: transparent transparent transparent #10B981; -fx-border-width: 0 0 0 4;";

    private static final String INACTIVE_STYLE = 
        "-fx-background-color: transparent; -fx-background-radius: 6; " +
        "-fx-text-fill: #4B5563; -fx-font-size: 13px; -fx-font-weight: bold; " +
        "-fx-cursor: hand; -fx-padding: 10 14; " +
        "-fx-border-color: transparent; -fx-border-width: 0;";

    private static class BadgeStyle {
        final String bg;
        final String text;
        BadgeStyle(String bg, String text) {
            this.bg = bg;
            this.text = text;
        }
    }

    // ---- Navigasi ----
    @FXML private Button btnNavPustaka, btnNavFavorit, btnNavPlaylist, btnNavStatistik, btnNavIO;
    @FXML private VBox panelPustaka, panelFavorit, panelPlaylist, panelStatistik, panelImportExport;
    private Button activeNavBtn;

    // ---- Header ----
    @FXML private TextField tfSearch;
    @FXML private Label lblUsername;

    // ---- Perpustakaan ----
    @FXML private ComboBox<String> cbGenre;
    @FXML private TableView<Song> tabelPustaka;
    @FXML private TableColumn<Song, Integer> colNoPustaka, colDurasiPustaka, colTahunPustaka;
    @FXML private TableColumn<Song, String> colIdPustaka, colJudulPustaka, colArtisPustaka, colGenrePustaka;
    @FXML private TableColumn<Song, Boolean> colFavPustaka;

    // ---- Form ----
    @FXML private TextField tfId, tfJudul, tfArtis, tfGenre, tfDurasi, tfTahun;
    @FXML private Button btnEdit, btnHapus;

    // ---- Favorit ----
    @FXML private TableView<Song> tabelFavorit;
    @FXML private TableColumn<Song, Integer> colNoFav, colDurasiFav, colTahunFav;
    @FXML private TableColumn<Song, String> colIdFav, colJudulFav, colArtisFav, colGenreFav;
    @FXML private TableColumn<Song, Boolean> colFavFav;

    // ---- Playlist ----
    @FXML private ListView<Playlist> listViewPlaylist;
    @FXML private TextField tfPlaylistNama;
    @FXML private CheckBox cbPublicPlaylist;
    @FXML private TableView<Song> tabelPlaylistSong;
    @FXML private TableColumn<Song, Integer> colNoPl, colDurasiPl, colTahunPl;
    @FXML private TableColumn<Song, String> colIdPl, colJudulPl, colArtisPl, colGenrePl;
    @FXML private TableColumn<Song, Boolean> colFavPl;
    @FXML private ComboBox<Song> cbLaguTersedia;

    // ---- Statistik ----
    @FXML private Label lblStatTotal, lblStatArtis, lblStatGenre, lblStatFavorit;
    @FXML private VBox genreStatBox;

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    @FXML
    public void initialize() {
        // Setup initial state
        btnNavPustaka.setStyle(ACTIVE_STYLE);
        btnNavFavorit.setStyle(INACTIVE_STYLE);
        btnNavPlaylist.setStyle(INACTIVE_STYLE);
        btnNavStatistik.setStyle(INACTIVE_STYLE);
        btnNavIO.setStyle(INACTIVE_STYLE);
        activeNavBtn = btnNavPustaka;

        // Init tables
        setupTable(tabelPustaka, colNoPustaka, colIdPustaka, colJudulPustaka, colArtisPustaka, colGenrePustaka, colDurasiPustaka, colTahunPustaka, colFavPustaka);
        setupTable(tabelFavorit, colNoFav, colIdFav, colJudulFav, colArtisFav, colGenreFav, colDurasiFav, colTahunFav, colFavFav);
        setupTable(tabelPlaylistSong, colNoPl, colIdPl, colJudulPl, colArtisPl, colGenrePl, colDurasiPl, colTahunPl, colFavPl);

        // Bind data
        tabelPustaka.setItems(manager.getDaftarLagu());
        listViewPlaylist.setItems(manager.getDaftarPlaylist());
        cbLaguTersedia.setItems(manager.getDaftarLagu());

        // Listeners
        tfSearch.textProperty().addListener((obs, o, n) -> applyFilters());
        cbGenre.setOnAction(e -> applyFilters());

        tabelPustaka.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                selectedSong = n;
                isiForm(n);
                btnEdit.setDisable(false);
                btnHapus.setDisable(false);
            } else {
                btnEdit.setDisable(true);
                btnHapus.setDisable(true);
            }
        });

        listViewPlaylist.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) tabelPlaylistSong.setItems(n.getLagu());
            else tabelPlaylistSong.setItems(FXCollections.observableArrayList());
        });

        btnEdit.setDisable(true);
        btnHapus.setDisable(true);

        refreshGenreFilter();
        setupPlaylistCellFactory();
    }

    /**
     * Custom cell factory untuk ListView playlist.
     * Menampilkan label [Public], [Milik Anda], atau [Lainnya] di depan nama playlist.
     */
    private void setupPlaylistCellFactory() {
        listViewPlaylist.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Playlist pl, boolean empty) {
                super.updateItem(pl, empty);
                if (empty || pl == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                User currentUser = manager.getCurrentUser();
                int currentUserId = currentUser != null ? currentUser.getId() : -1;
                boolean isOwner = pl.getUserId() == currentUserId;

                HBox row = new HBox(6);
                row.setAlignment(Pos.CENTER_LEFT);

                // Label visibilitas
                Label badge = new Label();
                badge.setPadding(new Insets(1, 6, 1, 6));
                badge.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4;");

                if (isOwner && pl.isPublic()) {
                    badge.setText("Public");
                    badge.setStyle(badge.getStyle() + "-fx-background-color: #DEF7EC; -fx-text-fill: #03543F;");
                } else if (isOwner) {
                    badge.setText("Milik Anda");
                    badge.setStyle(badge.getStyle() + "-fx-background-color: #E1EFFE; -fx-text-fill: #1E40AF;");
                } else {
                    badge.setText("Lainnya");
                    badge.setStyle(badge.getStyle() + "-fx-background-color: #F3E8FF; -fx-text-fill: #6B21A8;");
                }

                Label namaLabel = new Label(pl.getNama() + "  (" + pl.getLagu().size() + " lagu)");
                namaLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #111827;");

                row.getChildren().addAll(badge, namaLabel);
                setGraphic(row);
                setText(null);
            }
        });
    }

    private void setupTable(TableView<Song> tabel,
                            TableColumn<Song, Integer> colNo, TableColumn<Song, String> colId,
                            TableColumn<Song, String> colJudul, TableColumn<Song, String> colArtis,
                            TableColumn<Song, String> colGenre, TableColumn<Song, Integer> colDurasi,
                            TableColumn<Song, Integer> colTahun, TableColumn<Song, Boolean> colFav) {

        colNo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        colId.setCellValueFactory(d -> d.getValue().idProperty());
        colJudul.setCellValueFactory(d -> d.getValue().judulProperty());
        colArtis.setCellValueFactory(d -> d.getValue().artisProperty());
        colGenre.setCellValueFactory(d -> d.getValue().genreProperty());

        colGenre.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }
                Label badge = new Label(item);
                badge.setPadding(new Insets(2, 10, 2, 10));
                BadgeStyle bs = getGenreBadgeStyle(item);
                badge.setStyle("-fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold; " +
                        "-fx-text-fill: " + bs.text + "; -fx-background-color: " + bs.bg + ";");
                setGraphic(badge);
                setText(null);
            }
        });

        colDurasi.setCellValueFactory(d -> d.getValue().durasiProperty().asObject());
        colDurasi.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item + " min");
            }
        });

        colTahun.setCellValueFactory(d -> d.getValue().tahunProperty().asObject());

        colFav.setCellValueFactory(d -> d.getValue().favoritProperty().asObject());
        colFav.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button();
            {
                btn.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getTableView().getItems().size()) {
                        getTableView().getItems().get(idx).toggleFavorit();
                        refreshFavorit();
                    }
                });
            }
            @Override protected void updateItem(Boolean fav, boolean empty) {
                super.updateItem(fav, empty);
                if (empty || fav == null) { setGraphic(null); return; }
                btn.setText(fav ? "♥" : "♡");
                btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 16px; " +
                        "-fx-text-fill: " + (fav ? "#E91429;" : "#D1D5DB;"));
                setGraphic(btn);
                setText(null);
            }
        });
    }

    private BadgeStyle getGenreBadgeStyle(String genre) {
        if (genre == null) return new BadgeStyle("#F3F4F6", "#374151");
        return switch (genre.toLowerCase()) {
            case "pop" -> new BadgeStyle("#DEF7EC", "#03543F");
            case "rock" -> new BadgeStyle("#FDE8E8", "#9B1C1C");
            case "indie" -> new BadgeStyle("#F3E8FF", "#6B21A8");
            case "jazz" -> new BadgeStyle("#E1EFFE", "#1E40AF");
            case "r&b", "rnb" -> new BadgeStyle("#FEF3C7", "#92400E");
            case "hip-hop","hiphop","rap" -> new BadgeStyle("#FEF9C3", "#854D0E");
            case "klasik","classic" -> new BadgeStyle("#E0F2FE", "#075985");
            case "metal" -> new BadgeStyle("#F3F4F6", "#374151");
            default -> new BadgeStyle("#F3F4F6", "#374151");
        };
    }

    // ---- Navigation Actions ----

    @FXML private void onNavPustaka()   { navTo(btnNavPustaka, panelPustaka); }
    @FXML private void onNavFavorit()   { navTo(btnNavFavorit, panelFavorit); refreshFavorit(); }
    @FXML private void onNavPlaylist()  { navTo(btnNavPlaylist, panelPlaylist); }
    @FXML private void onNavStatistik() { navTo(btnNavStatistik, panelStatistik); refreshStatistik(); }
    @FXML private void onNavIO()        { navTo(btnNavIO, panelImportExport); }

    private void navTo(Button btn, VBox panel) {
        if (activeNavBtn != null) {
            activeNavBtn.setStyle(INACTIVE_STYLE);
        }
        btn.setStyle(ACTIVE_STYLE);
        activeNavBtn = btn;
        panelPustaka.setVisible(false); panelPustaka.setManaged(false);
        panelFavorit.setVisible(false); panelFavorit.setManaged(false);
        panelPlaylist.setVisible(false); panelPlaylist.setManaged(false);
        panelStatistik.setVisible(false); panelStatistik.setManaged(false);
        panelImportExport.setVisible(false); panelImportExport.setManaged(false);
        panel.setVisible(true); panel.setManaged(true);
    }

    @FXML
    private void onLogout() {
        if (confirm("Logout", "Apakah Anda yakin ingin logout?")) {
            manager.logout();
            if (onLogout != null) onLogout.run();
        }
    }

    // ---- CRUD Actions ----

    @FXML
    private void onTambahLagu() {
        String id = tfId.getText().trim();
        String judul = tfJudul.getText().trim();
        String artis = tfArtis.getText().trim();
        String genre = tfGenre.getText().trim();
        String sDur = tfDurasi.getText().trim();
        String sThn = tfTahun.getText().trim();

        if (id.isEmpty() || judul.isEmpty() || artis.isEmpty() || genre.isEmpty() || sDur.isEmpty() || sThn.isEmpty()) {
            alert("Peringatan", "Semua field harus diisi sebelum menambahkan lagu.");
            return;
        }
        try {
            int dur = Integer.parseInt(sDur);
            int thn = Integer.parseInt(sThn);
            Song lagu = new Song(id, judul, artis, genre, dur, thn);
            if (!manager.tambahLagu(lagu)) {
                alert("Duplikat ID", "ID \"" + id + "\" sudah digunakan oleh lagu lain.");
                return;
            }
            onBersihkanForm();
            refreshGenreFilter();
            alert("Berhasil", "🎵  Lagu \"" + judul + "\" berhasil ditambahkan!");
        } catch (NumberFormatException ex) {
            alert("Kesalahan Input", "Durasi dan Tahun harus berupa angka bulat.");
        }
    }

    @FXML
    private void onEditLagu() {
        if (selectedSong == null) return;
        String id = tfId.getText().trim();
        String judul = tfJudul.getText().trim();
        String artis = tfArtis.getText().trim();
        String genre = tfGenre.getText().trim();
        String sDur = tfDurasi.getText().trim();
        String sThn = tfTahun.getText().trim();

        if (id.isEmpty() || judul.isEmpty() || artis.isEmpty() || genre.isEmpty() || sDur.isEmpty() || sThn.isEmpty()) {
            alert("Peringatan", "Semua field harus diisi sebelum menyimpan.");
            return;
        }
        try {
            int dur = Integer.parseInt(sDur);
            int thn = Integer.parseInt(sThn);
            manager.editLagu(selectedSong, id, judul, artis, genre, dur, thn);
            onBersihkanForm();
            refreshGenreFilter();
            alert("Berhasil", "✅  Data lagu berhasil diperbarui!");
        } catch (NumberFormatException ex) {
            alert("Kesalahan Input", "Durasi dan Tahun harus berupa angka bulat.");
        }
    }

    @FXML
    private void onHapusLagu() {
        if (selectedSong == null) return;
        if (confirm("Hapus Lagu", "Hapus \"" + selectedSong.getJudul() + "\"?\nTindakan ini tidak dapat dibatalkan.")) {
            manager.hapusLagu(selectedSong);
            onBersihkanForm();
            refreshGenreFilter();
            refreshFavorit();
        }
    }

    @FXML
    private void onHapusSemuaLagu() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Apakah anda yakin ingin menghapus semua lagu?",
                ButtonType.YES, ButtonType.NO);
        a.setTitle("Hapus Semua Lagu");
        a.setHeaderText(null);
        Optional<ButtonType> res = a.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            manager.hapusSemuaLagu();
            // Reload semua data
            tabelPustaka.setItems(manager.getDaftarLagu());
            cbLaguTersedia.setItems(manager.getDaftarLagu());
            manager.muatPlaylist();
            listViewPlaylist.setItems(manager.getDaftarPlaylist());
            tabelPlaylistSong.setItems(FXCollections.observableArrayList());
            onBersihkanForm();
            refreshGenreFilter();
            refreshFavorit();
            alert("Berhasil", "🗑️  Semua lagu berhasil dihapus dari perpustakaan.");
        }
    }

    @FXML
    private void onBersihkanForm() {
        tfId.clear(); tfJudul.clear(); tfArtis.clear();
        tfGenre.clear(); tfDurasi.clear(); tfTahun.clear();
        selectedSong = null;
        tabelPustaka.getSelectionModel().clearSelection();
    }

    // ---- Filter & Refresh ----

    private void applyFilters() {
        String kw = tfSearch.getText();
        String genre = cbGenre.getSelectionModel().getSelectedItem();
        tabelPustaka.setItems(manager.filter(kw, genre));
    }

    private void refreshGenreFilter() {
        ObservableList<String> genres = FXCollections.observableArrayList("Semua Genre");
        genres.addAll(manager.getGenreUnik());
        String cur = cbGenre.getSelectionModel().getSelectedItem();
        cbGenre.setItems(genres);
        if (cur != null && genres.contains(cur)) cbGenre.getSelectionModel().select(cur);
        else cbGenre.getSelectionModel().selectFirst();
    }

    private void refreshFavorit() {
        tabelFavorit.setItems(manager.getDaftarFavorit());
    }

    private void isiForm(Song s) {
        tfId.setText(s.getId());
        tfJudul.setText(s.getJudul());
        tfArtis.setText(s.getArtis());
        tfGenre.setText(s.getGenre());
        tfDurasi.setText(String.valueOf(s.getDurasi()));
        tfTahun.setText(String.valueOf(s.getTahun()));
    }

    // ---- Playlist Actions ----

    @FXML
    private void onBuatPlaylist() {
        String nama = tfPlaylistNama.getText().trim();
        if (nama.isEmpty()) { alert("Peringatan", "Masukkan nama playlist."); return; }

        User currentUser = manager.getCurrentUser();
        if (currentUser == null) { alert("Error", "Anda belum login."); return; }

        boolean isPublic = cbPublicPlaylist != null && cbPublicPlaylist.isSelected();
        Playlist pl = new Playlist(nama, currentUser.getId(), isPublic);
        manager.tambahPlaylist(pl);
        tfPlaylistNama.clear();
        if (cbPublicPlaylist != null) cbPublicPlaylist.setSelected(false);
        listViewPlaylist.refresh();
    }

    @FXML
    private void onHapusPlaylist() {
        Playlist pl = listViewPlaylist.getSelectionModel().getSelectedItem();
        if (pl == null) { alert("Peringatan", "Pilih playlist yang ingin dihapus."); return; }

        // Hanya pemilik yang bisa menghapus
        User currentUser = manager.getCurrentUser();
        if (currentUser == null || pl.getUserId() != currentUser.getId()) {
            alert("Akses Ditolak", "Anda hanya bisa menghapus playlist milik Anda sendiri.");
            return;
        }

        if (confirm("Hapus Playlist", "Hapus playlist \"" + pl.getNama() + "\"?")) {
            manager.hapusPlaylist(pl);
            tabelPlaylistSong.setItems(FXCollections.observableArrayList());
            listViewPlaylist.refresh();
        }
    }

    @FXML
    private void onTogglePublicPlaylist() {
        Playlist pl = listViewPlaylist.getSelectionModel().getSelectedItem();
        if (pl == null) { alert("Peringatan", "Pilih playlist terlebih dahulu."); return; }

        User currentUser = manager.getCurrentUser();
        if (currentUser == null || pl.getUserId() != currentUser.getId()) {
            alert("Akses Ditolak", "Anda hanya bisa mengubah visibilitas playlist milik Anda sendiri.");
            return;
        }

        manager.togglePublicPlaylist(pl);
        listViewPlaylist.refresh();
        String status = pl.isPublic() ? "Public" : "Private";
        alert("Berhasil", "Playlist \"" + pl.getNama() + "\" sekarang berstatus " + status + ".");
    }

    @FXML
    private void onTambahKePlaylist() {
        Playlist pl = listViewPlaylist.getSelectionModel().getSelectedItem();
        Song lagu = cbLaguTersedia.getSelectionModel().getSelectedItem();
        if (pl == null) { alert("Peringatan", "Pilih playlist terlebih dahulu."); return; }
        if (lagu == null) { alert("Peringatan", "Pilih lagu dari dropdown terlebih dahulu."); return; }

        // Hanya pemilik yang bisa menambahkan lagu
        User currentUser = manager.getCurrentUser();
        if (currentUser == null || pl.getUserId() != currentUser.getId()) {
            alert("Akses Ditolak", "Anda hanya bisa menambah lagu ke playlist milik Anda sendiri.");
            return;
        }

        if (!pl.tambahLagu(lagu)) { alert("Info", "Lagu sudah ada di playlist ini."); return; }
        manager.simpanPlaylist();
        tabelPlaylistSong.setItems(pl.getLagu());
        listViewPlaylist.refresh();
    }

    @FXML
    private void onHapusDariPlaylist() {
        Playlist pl = listViewPlaylist.getSelectionModel().getSelectedItem();
        Song lagu = tabelPlaylistSong.getSelectionModel().getSelectedItem();
        if (pl == null || lagu == null) { alert("Peringatan", "Pilih playlist dan lagu."); return; }

        User currentUser = manager.getCurrentUser();
        if (currentUser == null || pl.getUserId() != currentUser.getId()) {
            alert("Akses Ditolak", "Anda hanya bisa menghapus lagu dari playlist milik Anda sendiri.");
            return;
        }

        pl.hapusLagu(lagu);
        manager.simpanPlaylist();
        listViewPlaylist.refresh();
    }

    // ---- Statistik ----

    private void refreshStatistik() {
        long favCnt = manager.getDaftarLagu().stream().filter(Song::isFavorit).count();
        lblStatTotal.setText(String.valueOf(manager.getDaftarLagu().size()));
        lblStatArtis.setText(String.valueOf(manager.getArtisUnik().size()));
        lblStatGenre.setText(String.valueOf(manager.getGenreUnik().size()));
        lblStatFavorit.setText(String.valueOf(favCnt));

        genreStatBox.getChildren().clear();
        Map<String, Long> stats = manager.getStatistikGenre();
        long total = manager.getDaftarLagu().size();
        String[] colors = {"#1DB954","#9D4EDD","#1E90FF","#E91429","#FF8C00","#00D4AA","#FFD700","#FF69B4"};
        int ci = 0;

        for (Map.Entry<String, Long> e : stats.entrySet()) {
            double pct = total > 0 ? (double) e.getValue() / total * 100 : 0;
            String color = colors[ci++ % colors.length];

            Label genreLbl = new Label(e.getKey());
            genreLbl.setStyle("-fx-text-fill:#111827; -fx-font-size:14px; -fx-min-width:100; -fx-pref-width:100;");

            HBox barContainer = new HBox();
            barContainer.setStyle("-fx-background-color:#F3F4F6; -fx-background-radius:4;");
            barContainer.setPrefHeight(8);
            HBox.setHgrow(barContainer, Priority.ALWAYS);

            Region fill = new Region();
            fill.setStyle("-fx-background-color:" + color + "; -fx-background-radius:4;");
            fill.setPrefHeight(8);
            fill.setPrefWidth(Math.max(4, pct * 4));
            barContainer.getChildren().add(fill);

            Label countLbl = new Label(e.getValue() + " lagu  (" + String.format("%.0f%%", pct) + ")");
            countLbl.setStyle("-fx-text-fill:#4B5563; -fx-font-size:12px; -fx-min-width:130; -fx-pref-width:130;");

            HBox row = new HBox(14, genreLbl, barContainer, countLbl);
            row.setAlignment(Pos.CENTER_LEFT);
            genreStatBox.getChildren().add(row);
        }
    }

    // ---- Import / Export ----

    @FXML
    private void onExportCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Data Lagu");
        fc.setInitialFileName("daftar_lagu.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showSaveDialog(tfSearch.getScene().getWindow());
        if (file != null) {
            try {
                manager.exportCSV(file);
                alert("Export Berhasil", "📤  " + manager.getDaftarLagu().size() + " lagu diekspor ke:\n" + file.getAbsolutePath());
            } catch (IOException ex) {
                alert("Export Gagal", "Gagal mengekspor: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void onImportCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Data Lagu dari CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showOpenDialog(tfSearch.getScene().getWindow());
        if (file != null) {
            try {
                int count = manager.importCSV(file);
                tabelPustaka.setItems(manager.getDaftarLagu());
                cbLaguTersedia.setItems(manager.getDaftarLagu());
                refreshGenreFilter();
                applyFilters();
                alert("Import Berhasil", "📥  " + count + " lagu berhasil diimpor dari:\n" + file.getAbsolutePath());
            } catch (IOException ex) {
                alert("Import Gagal", "Gagal mengimpor: " + ex.getMessage());
            }
        }
    }

    // ---- Helper Methods & Auto Save/Load ----

    private void alert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private boolean confirm(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        a.setTitle(title);
        a.setHeaderText(null);
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && res.get() == ButtonType.YES;
    }

    public void loadDataOtomatis() {
        // Load lagu dari SQLite
        tabelPustaka.setItems(manager.getDaftarLagu());
        cbLaguTersedia.setItems(manager.getDaftarLagu());

        // Jika DB kosong, import dari CSV awal
        if (manager.getDaftarLagu().isEmpty()) {
            File f = new File(DB_FILE);
            if (f.exists()) {
                try {
                    int count = manager.importCSV(f);
                    tabelPustaka.setItems(manager.getDaftarLagu());
                    cbLaguTersedia.setItems(manager.getDaftarLagu());
                    System.out.println("Auto-import dari CSV: " + count + " lagu dimuat.");
                } catch (IOException e) {
                    System.err.println("Gagal auto-import: " + e.getMessage());
                }
            }
        }

        // Load playlist (user-scoped)
        manager.muatPlaylist();
        listViewPlaylist.setItems(manager.getDaftarPlaylist());

        refreshGenreFilter();
        applyFilters();

        // Set label username di header
        User user = manager.getCurrentUser();
        if (user != null && lblUsername != null) {
            lblUsername.setText(user.getUsername());
        }
    }

    public void saveDataOtomatis() {
        // Tidak perlu save secara khusus karena data sudah langsung ditulis ke SQLite
        System.out.println("Data sudah tersimpan di SQLite.");
    }
    
    // Register shortcuts - dipanggil dari Main
    public void registerShortcuts(javafx.scene.Scene scene) {
        scene.setOnKeyPressed(e -> {
            boolean cmdOrCtrl = e.isControlDown() || e.isShortcutDown();
            if (cmdOrCtrl && e.getCode() == javafx.scene.input.KeyCode.F) {
                tfSearch.requestFocus();
            } else if (cmdOrCtrl && e.getCode() == javafx.scene.input.KeyCode.S) {
                onExportCSV();
            } else if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                onBersihkanForm();
            }
        });
    }
}
