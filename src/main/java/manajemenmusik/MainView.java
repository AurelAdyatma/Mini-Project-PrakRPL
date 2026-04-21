package manajemenmusik;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class MainView {

    // ---- Core ----
    private final BorderPane      root    = new BorderPane();
    private final MusicManager    manager = new MusicManager();

    // ---- Form fields ----
    private TextField tfId, tfJudul, tfArtis, tfGenre, tfDurasi, tfTahun;
    private TextField tfSearch;
    private ComboBox<String> cbGenre;

    // ---- Tables ----
    private TableView<Song> tabelPustaka;
    private TableView<Song> tabelFavorit;
    private TableView<Song> tabelPlaylistSong;

    // ---- Playlist UI ----
    private ListView<Playlist> listViewPlaylist;

    // ---- Content panels ----
    private VBox panelPustaka, panelFavorit, panelPlaylist,
                 panelStatistik, panelImportExport;
    private StackPane contentArea;

    // ---- Stat labels ----
    private Label lblStatTotal, lblStatArtis, lblStatGenre, lblStatFavorit;
    private VBox  genreStatBox;

    // ---- Nav state ----
    private Button activeNavBtn;
    private final Runnable onLogout;

    // ---- Edit state ----
    private Song selectedSong = null;

    // ================================================================
    public MainView(Runnable onLogout) {
        this.onLogout = onLogout;
        isiDataContoh();
        buatUI();
    }

    // ================================================================
    // UI SETUP
    // ================================================================
    private void buatUI() {
        root.getStyleClass().add("bg-main");
        root.setLeft(buatSidebar());
        root.setTop(buatHeader());

        // Build panels – favorites first so tabelFavorit exists for star callbacks
        panelFavorit       = buatPanelFavorit();
        panelPustaka       = buatPanelPustaka();
        panelPlaylist      = buatPanelPlaylist();
        panelStatistik     = buatPanelStatistik();
        panelImportExport  = buatPanelImportExport();

        contentArea = new StackPane(
                panelPustaka, panelFavorit, panelPlaylist,
                panelStatistik, panelImportExport);
        contentArea.getStyleClass().add("bg-main");
        contentArea.setPadding(new Insets(24, 28, 24, 28));

        tampilPanel(panelPustaka);
        root.setCenter(contentArea);
    }

    // ----------------------------------------------------------------
    // HEADER
    // ----------------------------------------------------------------
    private HBox buatHeader() {
        HBox header = new HBox(14);
        header.getStyleClass().add("bg-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 24, 10, 20));
        header.setStyle("-fx-border-color: transparent transparent #282828 transparent;" +
                        "-fx-border-width: 0 0 1 0;");

        Label logo = new Label("🎵");
        logo.setStyle("-fx-font-size: 22px;");

        VBox titleBox = new VBox(1);
        Label appName = new Label("MusikApp");
        appName.getStyleClass().add("label-app-title");
        Label appSub  = new Label("Music Manager");
        appSub.getStyleClass().add("label-app-subtitle");
        titleBox.getChildren().addAll(appName, appSub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        tfSearch = new TextField();
        tfSearch.setPromptText("🔍  Cari lagu, artis, genre...");
        tfSearch.getStyleClass().add("search-field");
        tfSearch.setPrefWidth(320);
        tfSearch.textProperty().addListener((obs, o, n) -> applyFilters());

        Label userLbl = new Label("👤 admin");
        userLbl.setStyle("-fx-text-fill: #B3B3B3; -fx-font-size: 13px; -fx-padding: 0 4;");

        header.getChildren().addAll(logo, titleBox, spacer, tfSearch, userLbl);
        return header;
    }

    // ----------------------------------------------------------------
    // SIDEBAR
    // ----------------------------------------------------------------
    private VBox buatSidebar() {
        VBox sidebar = new VBox(3);
        sidebar.getStyleClass().add("bg-sidebar");
        sidebar.setPadding(new Insets(14, 8, 14, 8));
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-border-color: transparent #282828 transparent transparent;" +
                         "-fx-border-width: 0 1 0 0;");

        Label secMenu = new Label("MENU");
        secMenu.getStyleClass().add("sidebar-section-label");

        Button btnPustaka   = buatNavBtn("📚   Perpustakaan");
        Button btnFavorit   = buatNavBtn("♥    Favorit");
        Button btnPlaylist  = buatNavBtn("📁   Playlist");

        Label secTools = new Label("TOOLS");
        secTools.getStyleClass().add("sidebar-section-label");
        VBox.setMargin(secTools, new Insets(12, 0, 0, 0));

        Button btnStatistik = buatNavBtn("📊   Statistik");
        Button btnIO        = buatNavBtn("💾   Import / Export");

        btnPustaka.setOnAction(e  -> navTo(btnPustaka,  panelPustaka));
        btnFavorit.setOnAction(e  -> { navTo(btnFavorit, panelFavorit);  refreshFavorit(); });
        btnPlaylist.setOnAction(e -> navTo(btnPlaylist, panelPlaylist));
        btnStatistik.setOnAction(e-> { navTo(btnStatistik, panelStatistik); refreshStatistik(); });
        btnIO.setOnAction(e       -> navTo(btnIO,       panelImportExport));

        // Default active
        btnPustaka.getStyleClass().setAll("nav-btn-active");
        activeNavBtn = btnPustaka;

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // --- Logout Button ---
        Button btnLogout = buatNavBtn("🚪   Logout");
        btnLogout.setStyle("-fx-text-fill: #E91429;"); // Red hint for logout
        btnLogout.setOnAction(e -> {
            if (confirm("Logout", "Apakah Anda yakin ingin logout?\nData akan disimpan otomatis.")) {
                saveDataOtomatis();
                onLogout.run();
            }
        });

        Label ver = new Label("v2.1.0  •  MusikApp");
        ver.setStyle("-fx-text-fill: #535353; -fx-font-size: 10px; -fx-padding: 0 14;");

        sidebar.getChildren().addAll(
                secMenu, btnPustaka, btnFavorit, btnPlaylist,
                secTools, btnStatistik, btnIO,
                spacer, btnLogout, ver);
        return sidebar;
    }

    private Button buatNavBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-btn");
        return btn;
    }

    private void navTo(Button btn, VBox panel) {
        if (activeNavBtn != null) activeNavBtn.getStyleClass().setAll("nav-btn");
        btn.getStyleClass().setAll("nav-btn-active");
        activeNavBtn = btn;
        tampilPanel(panel);
    }

    private void tampilPanel(VBox panel) {
        contentArea.getChildren().forEach(n -> { n.setVisible(false); n.setManaged(false); });
        panel.setVisible(true);
        panel.setManaged(true);
    }

    // ================================================================
    // PANEL: PERPUSTAKAAN
    // ================================================================
    private VBox buatPanelPustaka() {
        VBox panel = new VBox(16);

        // Title row
        Label title = new Label("📚  Perpustakaan Musik");
        title.getStyleClass().add("label-section-title");

        Label sub = new Label("Kelola seluruh koleksi musikmu di sini.");
        sub.getStyleClass().add("label-secondary");

        // Filter row
        cbGenre = new ComboBox<>();
        cbGenre.getStyleClass().add("combo-box");
        cbGenre.setPrefWidth(170);
        cbGenre.setPrefHeight(36);
        refreshGenreFilter();
        cbGenre.setOnAction(e -> applyFilters());

        Label filterLbl = new Label("Filter Genre:");
        filterLbl.setStyle("-fx-text-fill:#B3B3B3; -fx-font-size:13px;");

        HBox filterRow = new HBox(10, filterLbl, cbGenre);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        titleRow.getChildren().addAll(new VBox(4, title, sub), sp, filterRow);

        // Table
        tabelPustaka = buatTabelLagu(manager.getDaftarLagu());
        tabelPustaka.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> { if (n != null) { selectedSong = n; isiForm(n); } });
        VBox.setVgrow(tabelPustaka, Priority.ALWAYS);

        // Form card
        VBox form = buatFormCard();

        HBox content = new HBox(20, tabelPustaka, form);
        HBox.setHgrow(tabelPustaka, Priority.ALWAYS);
        VBox.setVgrow(content, Priority.ALWAYS);

        panel.getChildren().addAll(titleRow, content);
        return panel;
    }

    private VBox buatFormCard() {
        VBox form = new VBox(10);
        form.getStyleClass().add("bg-card");
        form.setPadding(new Insets(20));
        form.setPrefWidth(310);
        form.setMinWidth(310);
        form.setMaxWidth(310);

        Label formTitle = new Label("📝  Data Lagu");
        formTitle.setStyle("-fx-font-size:17px; -fx-font-weight:bold; -fx-text-fill:#FFFFFF;");

        Separator sep = new Separator();

        // Build fields
        tfId     = buatTextField("Contoh: 1025");
        tfJudul  = buatTextField("Masukkan judul...");
        tfArtis  = buatTextField("Nama artis...");
        tfGenre  = buatTextField("Pop, Rock, Indie...");
        tfDurasi = buatTextField("Contoh: 4");
        tfTahun  = buatTextField("Contoh: 2024");

        // Buttons
        Button btnTambah = new Button("➕  Tambah Lagu");
        btnTambah.getStyleClass().add("btn-green");
        btnTambah.setMaxWidth(Double.MAX_VALUE);
        btnTambah.setOnAction(e -> tambahLagu());

        Button btnEdit = new Button("✏️  Simpan Perubahan");
        btnEdit.getStyleClass().add("btn-orange");
        btnEdit.setMaxWidth(Double.MAX_VALUE);
        btnEdit.setOnAction(e -> editLagu());

        Button btnHapus = new Button("🗑️  Hapus Lagu");
        btnHapus.getStyleClass().add("btn-red");
        btnHapus.setMaxWidth(Double.MAX_VALUE);
        btnHapus.setOnAction(e -> hapusLagu());

        // Bind disable state to table selection
        btnEdit.disableProperty().bind(tabelPustaka.getSelectionModel().selectedItemProperty().isNull());
        btnHapus.disableProperty().bind(tabelPustaka.getSelectionModel().selectedItemProperty().isNull());

        Button btnReset = new Button("🔄  Bersihkan Form");
        btnReset.getStyleClass().add("btn-outline");
        btnReset.setMaxWidth(Double.MAX_VALUE);
        btnReset.setOnAction(e -> bersihkanForm());

        form.getChildren().addAll(
                formTitle, sep,
                field("ID Lagu",    tfId),
                field("Judul Lagu", tfJudul),
                field("Artis",      tfArtis),
                field("Genre",      tfGenre),
                field("Durasi (menit)", tfDurasi),
                field("Tahun Rilis",    tfTahun),
                new Separator(),
                btnTambah, btnEdit, btnHapus, btnReset
        );
        return form;
    }

    /** Wraps a label + textField into a VBox pair. */
    private VBox field(String labelText, TextField tf) {
        Label lbl = new Label(labelText.toUpperCase());
        lbl.getStyleClass().add("label-field");
        VBox box = new VBox(4, lbl, tf);
        return box;
    }

    private TextField buatTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("text-field");
        tf.setPrefHeight(38);
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    // ================================================================
    // PANEL: FAVORIT
    // ================================================================
    private VBox buatPanelFavorit() {
        VBox panel = new VBox(16);

        Label title = new Label("♥  Lagu Favorit");
        title.getStyleClass().add("label-section-title");

        Label sub = new Label("Klik ikon ♥ pada tabel untuk menambah atau menghapus lagu dari favorit.");
        sub.getStyleClass().add("label-secondary");
        sub.setWrapText(true);

        tabelFavorit = buatTabelLagu(manager.getDaftarFavorit());
        VBox.setVgrow(tabelFavorit, Priority.ALWAYS);

        panel.getChildren().addAll(title, sub, tabelFavorit);
        return panel;
    }

    // ================================================================
    // PANEL: PLAYLIST
    // ================================================================
    private VBox buatPanelPlaylist() {
        VBox panel = new VBox(16);

        Label title = new Label("📁  Manajemen Playlist");
        title.getStyleClass().add("label-section-title");

        HBox content = new HBox(20);
        VBox.setVgrow(content, Priority.ALWAYS);

        /* ---- LEFT: daftar playlist + buat/hapus ---- */
        VBox leftBox = new VBox(12);
        leftBox.getStyleClass().add("bg-card");
        leftBox.setPadding(new Insets(18));
        leftBox.setPrefWidth(260);
        leftBox.setMinWidth(260);

        Label lblDaftar = new Label("Daftar Playlist");
        lblDaftar.getStyleClass().add("label-card-title");

        listViewPlaylist = new ListView<>(manager.getDaftarPlaylist());
        listViewPlaylist.getStyleClass().add("list-view");
        listViewPlaylist.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");
        VBox.setVgrow(listViewPlaylist, Priority.ALWAYS);
        listViewPlaylist.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Playlist item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });

        TextField tfNama = buatTextField("Nama playlist baru...");

        Button btnBuat = new Button("➕  Buat Playlist");
        btnBuat.getStyleClass().add("btn-green");
        btnBuat.setMaxWidth(Double.MAX_VALUE);
        btnBuat.setOnAction(e -> {
            String nama = tfNama.getText().trim();
            if (nama.isEmpty()) { alert("Peringatan", "Masukkan nama playlist."); return; }
            manager.tambahPlaylist(new Playlist(nama));
            tfNama.clear();
            listViewPlaylist.refresh();
        });

        Button btnHapusPl = new Button("🗑️  Hapus Playlist");
        btnHapusPl.getStyleClass().add("btn-red");
        btnHapusPl.setMaxWidth(Double.MAX_VALUE);
        btnHapusPl.setOnAction(e -> {
            Playlist pl = listViewPlaylist.getSelectionModel().getSelectedItem();
            if (pl == null) { alert("Peringatan", "Pilih playlist yang ingin dihapus."); return; }
            if (confirm("Hapus Playlist", "Hapus playlist \"" + pl.getNama() + "\"?")) {
                manager.hapusPlaylist(pl);
                tabelPlaylistSong.setItems(FXCollections.observableArrayList());
                listViewPlaylist.refresh();
            }
        });

        leftBox.getChildren().addAll(lblDaftar, listViewPlaylist, tfNama, btnBuat, btnHapusPl);

        /* ---- RIGHT: isi playlist + aksi ---- */
        VBox rightBox = new VBox(12);
        rightBox.getStyleClass().add("bg-card");
        rightBox.setPadding(new Insets(18));
        HBox.setHgrow(rightBox, Priority.ALWAYS);

        Label lblIsi = new Label("Isi Playlist");
        lblIsi.getStyleClass().add("label-card-title");

        Label lblHint = new Label("Pilih playlist di kiri, lalu pilih lagu dari Perpustakaan, kemudian klik Tambahkan.");
        lblHint.getStyleClass().add("label-secondary");
        lblHint.setWrapText(true);

        tabelPlaylistSong = buatTabelLagu(FXCollections.observableArrayList());
        VBox.setVgrow(tabelPlaylistSong, Priority.ALWAYS);

        // When playlist selected, show its songs
        listViewPlaylist.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) tabelPlaylistSong.setItems(n.getLagu());
            else tabelPlaylistSong.setItems(FXCollections.observableArrayList());
        });

        Button btnAdd = new Button("➕  Tambahkan Lagu dari Perpustakaan");
        btnAdd.getStyleClass().add("btn-purple");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setOnAction(e -> {
            Playlist pl   = listViewPlaylist.getSelectionModel().getSelectedItem();
            Song     lagu = tabelPustaka    .getSelectionModel().getSelectedItem();
            if (pl   == null) { alert("Peringatan", "Pilih playlist terlebih dahulu."); return; }
            if (lagu == null) { alert("Peringatan", "Pilih lagu dari tabel Perpustakaan."); return; }
            if (!pl.tambahLagu(lagu)) { alert("Info", "Lagu sudah ada di playlist ini."); return; }
            tabelPlaylistSong.setItems(pl.getLagu());
            listViewPlaylist.refresh();
        });

        Button btnDel = new Button("🗑️  Hapus dari Playlist");
        btnDel.getStyleClass().add("btn-red");
        btnDel.setMaxWidth(Double.MAX_VALUE);
        btnDel.setOnAction(e -> {
            Playlist pl   = listViewPlaylist.getSelectionModel().getSelectedItem();
            Song     lagu = tabelPlaylistSong.getSelectionModel().getSelectedItem();
            if (pl == null || lagu == null) { alert("Peringatan", "Pilih playlist dan lagu."); return; }
            pl.hapusLagu(lagu);
            listViewPlaylist.refresh();
        });

        HBox actRow = new HBox(10, btnAdd, btnDel);
        rightBox.getChildren().addAll(lblIsi, lblHint, tabelPlaylistSong, actRow);

        content.getChildren().addAll(leftBox, rightBox);
        panel.getChildren().addAll(title, content);
        return panel;
    }

    // ================================================================
    // PANEL: STATISTIK
    // ================================================================
    private VBox buatPanelStatistik() {
        VBox panel = new VBox(22);

        Label title = new Label("📊  Statistik Musik");
        title.getStyleClass().add("label-section-title");

        // Stat cards
        lblStatTotal  = new Label("0"); lblStatTotal .getStyleClass().add("label-stat-value-green");
        lblStatArtis  = new Label("0"); lblStatArtis .getStyleClass().add("label-stat-value-purple");
        lblStatGenre  = new Label("0"); lblStatGenre .getStyleClass().add("label-stat-value-blue");
        lblStatFavorit= new Label("0"); lblStatFavorit.getStyleClass().add("label-stat-value-red");

        HBox cards = new HBox(16,
                statCard("🎵  Total Lagu",    lblStatTotal,  "stat-card-green"),
                statCard("🎤  Total Artis",   lblStatArtis,  "stat-card-purple"),
                statCard("🎸  Total Genre",   lblStatGenre,  "stat-card-blue"),
                statCard("♥   Favorit",       lblStatFavorit,"stat-card-red")
        );
        cards.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Genre distribution
        Label genreTitle = new Label("Distribusi Genre");
        genreTitle.setStyle("-fx-font-size:18px; -fx-font-weight:bold; -fx-text-fill:#FFFFFF;");

        genreStatBox = new VBox(12);
        genreStatBox.getStyleClass().add("bg-card");
        genreStatBox.setPadding(new Insets(20));

        ScrollPane sp = new ScrollPane(genreStatBox);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(sp, Priority.ALWAYS);

        panel.getChildren().addAll(title, cards, genreTitle, sp);
        return panel;
    }

    private VBox statCard(String label, Label valueLabel, String styleClass) {
        VBox card = new VBox(6);
        card.getStyleClass().add(styleClass);
        card.setAlignment(Pos.TOP_LEFT);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:12px; -fx-text-fill:#B3B3B3; -fx-font-weight:bold;");
        card.getChildren().addAll(valueLabel, lbl);
        return card;
    }

    private void refreshStatistik() {
        long favCnt = manager.getDaftarLagu().stream().filter(Song::isFavorit).count();
        lblStatTotal  .setText(String.valueOf(manager.getDaftarLagu().size()));
        lblStatArtis  .setText(String.valueOf(manager.getArtisUnik().size()));
        lblStatGenre  .setText(String.valueOf(manager.getGenreUnik().size()));
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
            genreLbl.setStyle("-fx-text-fill:#FFFFFF; -fx-font-size:14px; -fx-min-width:100; -fx-pref-width:100;");

            HBox barContainer = new HBox();
            barContainer.setStyle("-fx-background-color:#282828; -fx-background-radius:4;");
            barContainer.setPrefHeight(8);
            HBox.setHgrow(barContainer, Priority.ALWAYS);

            Region fill = new Region();
            fill.setStyle("-fx-background-color:" + color + "; -fx-background-radius:4;");
            fill.setPrefHeight(8);
            fill.setPrefWidth(Math.max(4, pct * 4));
            barContainer.getChildren().add(fill);

            Label countLbl = new Label(e.getValue() + " lagu  (" + String.format("%.0f%%", pct) + ")");
            countLbl.setStyle("-fx-text-fill:#B3B3B3; -fx-font-size:12px; -fx-min-width:130; -fx-pref-width:130;");

            HBox row = new HBox(14, genreLbl, barContainer, countLbl);
            row.setAlignment(Pos.CENTER_LEFT);
            genreStatBox.getChildren().add(row);
        }
    }

    // ================================================================
    // PANEL: IMPORT / EXPORT
    // ================================================================
    private VBox buatPanelImportExport() {
        VBox panel = new VBox(24);
        panel.setMaxWidth(600);

        Label title = new Label("💾  Import & Export Data");
        title.getStyleClass().add("label-section-title");

        // Export card
        VBox exportCard = new VBox(14);
        exportCard.getStyleClass().add("bg-card");
        exportCard.setPadding(new Insets(24));

        Label exportTitle = new Label("📤  Export ke CSV");
        exportTitle.setStyle("-fx-font-size:17px; -fx-font-weight:bold; -fx-text-fill:#FFFFFF;");

        Label exportDesc = new Label(
                "Simpan seluruh data lagu ke file CSV. File dapat dibuka dengan " +
                "Microsoft Excel atau Google Sheets.");
        exportDesc.getStyleClass().add("label-secondary");
        exportDesc.setWrapText(true);

        Button btnExport = new Button("📤  Export Semua Lagu ke CSV");
        btnExport.getStyleClass().add("btn-green");
        btnExport.setOnAction(e -> exportCSV());

        exportCard.getChildren().addAll(exportTitle, exportDesc, btnExport);

        // Import card
        VBox importCard = new VBox(14);
        importCard.getStyleClass().add("bg-card");
        importCard.setPadding(new Insets(24));

        Label importTitle = new Label("📥  Import dari CSV");
        importTitle.setStyle("-fx-font-size:17px; -fx-font-weight:bold; -fx-text-fill:#FFFFFF;");

        Label importDesc = new Label(
                "Muat data lagu dari file CSV. Format kolom yang diharapkan:\n" +
                "ID, Judul, Artis, Genre, Durasi, Tahun, Favorit (opsional).\n" +
                "Data akan ditambahkan ke perpustakaan yang sudah ada. " +
                "Lagu dengan ID yang sama akan dilewati.");
        importDesc.getStyleClass().add("label-secondary");
        importDesc.setWrapText(true);

        Button btnImport = new Button("📥  Import Lagu dari CSV");
        btnImport.getStyleClass().add("btn-purple");
        btnImport.setOnAction(e -> importCSV());

        importCard.getChildren().addAll(importTitle, importDesc, btnImport);

        panel.getChildren().addAll(title, exportCard, importCard);
        return panel;
    }

    // ================================================================
    // TABLE BUILDER
    // ================================================================
    @SuppressWarnings("unchecked")
    private TableView<Song> buatTabelLagu(ObservableList<Song> items) {
        TableView<Song> tabel = new TableView<>(items);
        tabel.getStyleClass().add("table-view");
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        Label placeholder = new Label("Belum ada data lagu");
        placeholder.setStyle("-fx-text-fill:#B3B3B3; -fx-font-size:14px;");
        tabel.setPlaceholder(placeholder);

        // No. column
        TableColumn<Song, Integer> colNo = new TableColumn<>("#");
        colNo.setPrefWidth(45);
        colNo.setSortable(false);
        colNo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        TableColumn<Song, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> d.getValue().idProperty());
        colId.setPrefWidth(65);

        TableColumn<Song, String> colJudul = new TableColumn<>("JUDUL");
        colJudul.setCellValueFactory(d -> d.getValue().judulProperty());
        colJudul.setPrefWidth(210);

        TableColumn<Song, String> colArtis = new TableColumn<>("ARTIS");
        colArtis.setCellValueFactory(d -> d.getValue().artisProperty());
        colArtis.setPrefWidth(160);

        TableColumn<Song, String> colGenre = new TableColumn<>("GENRE");
        colGenre.setCellValueFactory(d -> d.getValue().genreProperty());
        colGenre.setPrefWidth(90);
        // Colored genre badge
        colGenre.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }
                Label badge = new Label(item);
                badge.setPadding(new Insets(2, 10, 2, 10));
                badge.setStyle("-fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #000000; " +
                        "-fx-background-color: " + genreColor(item) + ";");
                setGraphic(badge);
                setText(null);
            }
        });

        TableColumn<Song, Integer> colDurasi = new TableColumn<>("DURASI");
        colDurasi.setCellValueFactory(d -> d.getValue().durasiProperty().asObject());
        colDurasi.setPrefWidth(80);
        colDurasi.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item + " min");
            }
        });

        TableColumn<Song, Integer> colTahun = new TableColumn<>("TAHUN");
        colTahun.setCellValueFactory(d -> d.getValue().tahunProperty().asObject());
        colTahun.setPrefWidth(75);

        // Favorit column with heart toggle
        TableColumn<Song, Boolean> colFav = new TableColumn<>("♥");
        colFav.setCellValueFactory(d -> d.getValue().favoritProperty().asObject());
        colFav.setPrefWidth(55);
        colFav.setSortable(false);
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
                        "-fx-text-fill: " + (fav ? "#E91429;" : "#535353;"));
                setGraphic(btn);
                setText(null);
            }
        });

        tabel.getColumns().addAll(colNo, colId, colJudul, colArtis, colGenre, colDurasi, colTahun, colFav);
        return tabel;
    }

    /** Returns a color hex string for a genre name. */
    private String genreColor(String genre) {
        if (genre == null) return "#B3B3B3";
        return switch (genre.toLowerCase()) {
            case "pop"    -> "#1DB954";
            case "rock"   -> "#E91429";
            case "indie"  -> "#9D4EDD";
            case "jazz"   -> "#1E90FF";
            case "r&b", "rnb" -> "#FF8C00";
            case "hip-hop","hiphop","rap" -> "#FFD700";
            case "klasik","classic"       -> "#00D4AA";
            case "metal"  -> "#A0A0A0";
            default       -> "#607D8B";
        };
    }

    // ================================================================
    // ACTIONS
    // ================================================================

    private void tambahLagu() {
        String id     = tfId.getText().trim();
        String judul  = tfJudul.getText().trim();
        String artis  = tfArtis.getText().trim();
        String genre  = tfGenre.getText().trim();
        String sDur   = tfDurasi.getText().trim();
        String sThn   = tfTahun.getText().trim();

        if (id.isEmpty() || judul.isEmpty() || artis.isEmpty() ||
                genre.isEmpty() || sDur.isEmpty() || sThn.isEmpty()) {
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
            bersihkanForm();
            refreshGenreFilter();
            alert("Berhasil", "🎵  Lagu \"" + judul + "\" berhasil ditambahkan!");
        } catch (NumberFormatException ex) {
            alert("Kesalahan Input", "Durasi dan Tahun harus berupa angka bulat.");
        }
    }

    private void editLagu() {
        if (selectedSong == null) {
            alert("Peringatan", "Pilih lagu dari tabel yang ingin diedit terlebih dahulu.");
            return;
        }
        String id    = tfId.getText().trim();
        String judul = tfJudul.getText().trim();
        String artis = tfArtis.getText().trim();
        String genre = tfGenre.getText().trim();
        String sDur  = tfDurasi.getText().trim();
        String sThn  = tfTahun.getText().trim();

        if (id.isEmpty() || judul.isEmpty() || artis.isEmpty() ||
                genre.isEmpty() || sDur.isEmpty() || sThn.isEmpty()) {
            alert("Peringatan", "Semua field harus diisi sebelum menyimpan.");
            return;
        }
        try {
            int dur = Integer.parseInt(sDur);
            int thn = Integer.parseInt(sThn);
            manager.editLagu(selectedSong, id, judul, artis, genre, dur, thn);
            bersihkanForm();
            refreshGenreFilter();
            alert("Berhasil", "✅  Data lagu berhasil diperbarui!");
        } catch (NumberFormatException ex) {
            alert("Kesalahan Input", "Durasi dan Tahun harus berupa angka bulat.");
        }
    }

    private void hapusLagu() {
        if (selectedSong == null) {
            alert("Peringatan", "Pilih lagu dari tabel yang ingin dihapus.");
            return;
        }
        if (confirm("Hapus Lagu", "Hapus \"" + selectedSong.getJudul() + "\"?\nTindakan ini tidak dapat dibatalkan.")) {
            manager.hapusLagu(selectedSong);
            bersihkanForm();
            refreshGenreFilter();
            refreshFavorit();
        }
    }

    private void applyFilters() {
        if (tabelPustaka == null) return;
        String kw    = tfSearch != null ? tfSearch.getText() : "";
        String genre = cbGenre  != null ? cbGenre.getSelectionModel().getSelectedItem() : null;
        tabelPustaka.setItems(manager.filter(kw, genre));
    }

    private void refreshGenreFilter() {
        if (cbGenre == null) return;
        ObservableList<String> genres = FXCollections.observableArrayList("Semua Genre");
        genres.addAll(manager.getGenreUnik());
        String cur = cbGenre.getSelectionModel().getSelectedItem();
        cbGenre.setItems(genres);
        if (cur != null && genres.contains(cur)) cbGenre.getSelectionModel().select(cur);
        else cbGenre.getSelectionModel().selectFirst();
    }

    private void refreshFavorit() {
        if (tabelFavorit != null) tabelFavorit.setItems(manager.getDaftarFavorit());
    }

    private void isiForm(Song s) {
        tfId   .setText(s.getId());
        tfJudul.setText(s.getJudul());
        tfArtis.setText(s.getArtis());
        tfGenre.setText(s.getGenre());
        tfDurasi.setText(String.valueOf(s.getDurasi()));
        tfTahun.setText(String.valueOf(s.getTahun()));
    }

    private void bersihkanForm() {
        tfId.clear(); tfJudul.clear(); tfArtis.clear();
        tfGenre.clear(); tfDurasi.clear(); tfTahun.clear();
        selectedSong = null;
        if (tabelPustaka != null) tabelPustaka.getSelectionModel().clearSelection();
    }

    private void exportCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Data Lagu");
        fc.setInitialFileName("daftar_lagu.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showSaveDialog(root.getScene().getWindow());
        if (file != null) {
            try {
                manager.exportCSV(file);
                alert("Export Berhasil",
                        "📤  " + manager.getDaftarLagu().size() + " lagu diekspor ke:\n" + file.getAbsolutePath());
            } catch (IOException ex) {
                alert("Export Gagal", "Gagal mengekspor: " + ex.getMessage());
            }
        }
    }

    private void importCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Data Lagu dari CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            try {
                int count = manager.importCSV(file);
                refreshGenreFilter();
                applyFilters();
                alert("Import Berhasil", "📥  " + count + " lagu berhasil diimpor dari:\n" + file.getAbsolutePath());
            } catch (IOException ex) {
                alert("Import Gagal", "Gagal mengimpor: " + ex.getMessage());
            }
        }
    }

    // ================================================================
    // HELPERS
    // ================================================================
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

    // ================================================================
    // SAMPLE DATA
    // ================================================================
    private void isiDataContoh() {
        manager.tambahLagu(new Song("0097", "Pelangi Di Matamu",             "Jamrud",           "Rock",  5, 1999));
        manager.tambahLagu(new Song("0098", "Hati-Hati di Jalan",            "Tulus",            "Pop",   4, 2022));
        manager.tambahLagu(new Song("0099", "Zona Nyaman",                   "Fourtwnty",        "Indie", 4, 2017));
        manager.tambahLagu(new Song("1000", "Kuning",                        "Rumahsakit",       "Pop",   4, 2002));
        manager.tambahLagu(new Song("1001", "Monokrom",                      "Tulus",            "Pop",   4, 2016));
        manager.tambahLagu(new Song("1002", "Interaksi",                     "Tulus",            "Pop",   4, 2014));
        manager.tambahLagu(new Song("1003", "Sial",                          "Mahalini",         "Pop",   4, 2021));
        manager.tambahLagu(new Song("1004", "Melukis Senja",                 "Budi Doremi",      "Pop",   5, 2019));
        manager.tambahLagu(new Song("1005", "Komang",                        "Raim Laode",       "Pop",   4, 2023));
        manager.tambahLagu(new Song("1006", "Tak Ingin Usai",                "Keisya Levronka",  "Pop",   4, 2021));
        manager.tambahLagu(new Song("1007", "Mesin Waktu",                   "Budi Doremi",      "Pop",   4, 2020));
        manager.tambahLagu(new Song("1008", "Akad",                          "Payung Teduh",     "Indie", 4, 2012));
        manager.tambahLagu(new Song("1009", "Resah",                         "Payung Teduh",     "Indie", 5, 2012));
        manager.tambahLagu(new Song("1010", "Untuk Perempuan...",            "Payung Teduh",     "Indie", 5, 2015));
        manager.tambahLagu(new Song("1011", "Amin Paling Serius",            "Sal Priadi",       "Indie", 4, 2018));
        manager.tambahLagu(new Song("1012", "Kita ke Sana",                  "Hindia",           "Indie", 4, 2019));
        manager.tambahLagu(new Song("1013", "Evaluasi",                      "Hindia",           "Indie", 5, 2019));
        manager.tambahLagu(new Song("1014", "Secukupnya",                    "Hindia",           "Indie", 5, 2019));
        manager.tambahLagu(new Song("1015", "Rumah ke Rumah",                "Hindia",           "Indie", 4, 2019));
        manager.tambahLagu(new Song("1016", "Bertaut",                       "Nadin Amizah",     "Pop",   5, 2020));
        manager.tambahLagu(new Song("1017", "Sorai",                         "Nadin Amizah",     "Pop",   4, 2021));
        manager.tambahLagu(new Song("1018", "Rumpang",                       "Nadin Amizah",     "Pop",   5, 2021));
        manager.tambahLagu(new Song("1019", "Celengan Rindu",                "Fiersa Besari",    "Pop",   4, 2018));
        manager.tambahLagu(new Song("1020", "April",                         "Fiersa Besari",    "Pop",   4, 2018));
        manager.tambahLagu(new Song("1021", "Waktu yang Salah",              "Fiersa Besari",    "Pop",   5, 2018));
        manager.tambahLagu(new Song("1022", "Cinta Luar Biasa",              "Andmesh",          "Pop",   4, 2019));
        manager.tambahLagu(new Song("1023", "Sampai Jadi Debu",              "Banda Neira",      "Indie", 5, 2015));
        manager.tambahLagu(new Song("1024", "Yang Patah Tumbuh",             "Banda Neira",      "Indie", 5, 2015));
        manager.tambahLagu(new Song("1025", "Runtuh",                        "Feby Putri",       "Jazz",  5, 2020));
        manager.tambahLagu(new Song("1026", "Tenang",                        "Yura Yunita",      "Pop",   4, 2015));

        // Pre-favorite beberapa lagu
        manager.getDaftarLagu().get(0).setFavorit(true);
        manager.getDaftarLagu().get(1).setFavorit(true);
        manager.getDaftarLagu().get(7).setFavorit(true);
    }

    // ================================================================
    // PERSISTENCE & SHORTCUTS
    // ================================================================

    private static final String DB_FILE = "music_database.csv";

    public void loadDataOtomatis() {
        File f = new File(DB_FILE);
        if (f.exists()) {
            try {
                int count = manager.importCSV(f);
                refreshGenreFilter();
                applyFilters();
                System.out.println("Auto-load berhasil: " + count + " lagu dimuat.");
            } catch (IOException e) {
                System.err.println("Gagal auto-load: " + e.getMessage());
            }
        }
    }

    public void saveDataOtomatis() {
        try {
            manager.exportCSV(new File(DB_FILE));
            System.out.println("Auto-save berhasil.");
        } catch (IOException e) {
            System.err.println("Gagal auto-save: " + e.getMessage());
        }
    }

    public void registerShortcuts(javafx.scene.Scene scene) {
        scene.setOnKeyPressed(e -> {
            boolean cmdOrCtrl = e.isControlDown() || e.isShortcutDown();
            
            // CMD/CTRL + F -> Search
            if (cmdOrCtrl && e.getCode() == javafx.scene.input.KeyCode.F) {
                tfSearch.requestFocus();
            }
            // CMD/CTRL + S -> Export
            else if (cmdOrCtrl && e.getCode() == javafx.scene.input.KeyCode.S) {
                exportCSV();
            }
            // ESC -> Reset
            else if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                bersihkanForm();
            }
        });
    }

    public Parent getView() {
        return root;
    }
}
