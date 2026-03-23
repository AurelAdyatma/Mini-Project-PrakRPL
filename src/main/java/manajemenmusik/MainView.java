package manajemenmusik;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainView {
    private final BorderPane root;
    private final MusicManager manager;
    private final TableView<Song> tabel;

    private TextField tfId;
    private TextField tfJudul;
    private TextField tfArtis;
    private TextField tfGenre;
    private TextField tfDurasi;
    private TextField tfCari;

    public MainView() {
        root = new BorderPane();
        manager = new MusicManager();
        tabel = new TableView<>();

        buatForm();
        buatTabel();
        buatLayout();
        isiDataContoh();
    }

    private void buatForm() {
        tfId = buatTextField("Masukkan ID lagu");
        tfJudul = buatTextField("Masukkan judul lagu");
        tfArtis = buatTextField("Masukkan nama artis");
        tfGenre = buatTextField("Masukkan genre lagu");
        tfDurasi = buatTextField("Masukkan durasi lagu");
        tfCari = buatTextField("Cari berdasarkan judul, artis, atau genre");
    }

    private TextField buatTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setPrefHeight(40);
        textField.setStyle(
                "-fx-background-color: #F8FBFF;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #BFDBFE;" +
                        "-fx-padding: 10;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: 'Segoe UI';"
        );
        return textField;
    }

    private void buatTabel() {
        TableColumn<Song, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Song, String> colJudul = new TableColumn<>("Judul");
        colJudul.setCellValueFactory(new PropertyValueFactory<>("judul"));

        TableColumn<Song, String> colArtis = new TableColumn<>("Artis");
        colArtis.setCellValueFactory(new PropertyValueFactory<>("artis"));

        TableColumn<Song, String> colGenre = new TableColumn<>("Genre");
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

        TableColumn<Song, Integer> colDurasi = new TableColumn<>("Durasi");
        colDurasi.setCellValueFactory(new PropertyValueFactory<>("durasi"));

        tabel.getColumns().addAll(colId, colJudul, colArtis, colGenre, colDurasi);
        tabel.setItems(manager.getDaftarLagu());
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabel.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-color: #DBEAFE;" +
                        "-fx-padding: 10;" +
                        "-fx-font-family: 'Segoe UI';"
        );
        tabel.setPrefHeight(560);
    }

    private void buatLayout() {
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #0F3D91, #2563EB, #93C5FD);" +
                        "-fx-font-family: 'Segoe UI';"
        );

        Label lblHeader = new Label("MiniProject Aplikasi Manajemen Musik");
        lblHeader.setStyle(
                "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );

        Label lblSubHeader = new Label("Kelola data lagu dengan tampilan modern, rapi, dan nyaman digunakan");
        lblSubHeader.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #DBEAFE;"
        );

        VBox headerBox = new VBox(8, lblHeader, lblSubHeader);
        headerBox.setPadding(new Insets(24, 28, 18, 28));

        VBox formCard = new VBox(12);
        formCard.setPadding(new Insets(22));
        formCard.setPrefWidth(350);
        formCard.setStyle(
                "-fx-background-color: rgba(255,255,255,0.96);" +
                        "-fx-background-radius: 24;" +
                        "-fx-border-radius: 24;" +
                        "-fx-border-color: rgba(255,255,255,0.35);" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 18, 0, 0, 6);"
        );

        Label lblForm = new Label("Form Data Lagu");
        lblForm.setStyle(
                "-fx-font-size: 23px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #12356E;"
        );

        Label lblFormDesc = new Label("Isi data lagu dengan lengkap untuk ditambahkan ke tabel");
        lblFormDesc.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #64748B;"
        );

        Button btnTambah = buatTombol("Tambah Lagu", "#1D4ED8");
        Button btnHapus = buatTombol("Hapus Lagu", "#DC2626");
        Button btnCari = buatTombol("Cari Lagu", "#0F766E");
        Button btnTampilSemua = buatTombol("Tampilkan Semua", "#7C3AED");

        btnTambah.setOnAction(e -> tambahLagu());
        btnHapus.setOnAction(e -> hapusLagu());
        btnCari.setOnAction(e -> cariLagu());
        btnTampilSemua.setOnAction(e -> tabel.setItems(manager.getDaftarLagu()));

        HBox tombolAksi1 = new HBox(10, btnTambah, btnHapus);
        HBox tombolAksi2 = new HBox(10, btnCari, btnTampilSemua);
        tombolAksi1.setAlignment(Pos.CENTER);
        tombolAksi2.setAlignment(Pos.CENTER);

        formCard.getChildren().addAll(
                lblForm,
                lblFormDesc,
                new Separator(),
                buatLabelField("ID Lagu"),
                tfId,
                buatLabelField("Judul Lagu"),
                tfJudul,
                buatLabelField("Artis"),
                tfArtis,
                buatLabelField("Genre"),
                tfGenre,
                buatLabelField("Durasi"),
                tfDurasi,
                tombolAksi1,
                new Separator(),
                buatLabelField("Pencarian Lagu"),
                tfCari,
                tombolAksi2
        );

        VBox tabelCard = new VBox(12);
        tabelCard.setPadding(new Insets(22));
        tabelCard.setStyle(
                "-fx-background-color: rgba(255,255,255,0.96);" +
                        "-fx-background-radius: 24;" +
                        "-fx-border-radius: 24;" +
                        "-fx-border-color: rgba(255,255,255,0.35);" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 18, 0, 0, 6);"
        );

        Label lblTabel = new Label("Daftar Lagu");
        lblTabel.setStyle(
                "-fx-font-size: 23px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #12356E;"
        );

        Label lblTabelDesc = new Label("Semua data lagu yang tersedia akan tampil pada tabel berikut");
        lblTabelDesc.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #64748B;"
        );

        tabelCard.getChildren().addAll(lblTabel, lblTabelDesc, tabel);

        HBox contentBox = new HBox(22, formCard, tabelCard);
        contentBox.setPadding(new Insets(0, 28, 28, 28));

        root.setTop(headerBox);
        root.setCenter(contentBox);
    }

    private Label buatLabelField(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1E293B;"
        );
        return label;
    }

    private Button buatTombol(String text, String warna) {
        Button button = new Button(text);
        button.setPrefWidth(145);
        button.setPrefHeight(42);
        button.setStyle(
                "-fx-background-color: " + warna + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 14;" +
                        "-fx-cursor: hand;"
        );
        return button;
    }

    private void tambahLagu() {
        try {
            String id = tfId.getText().trim();
            String judul = tfJudul.getText().trim();
            String artis = tfArtis.getText().trim();
            String genre = tfGenre.getText().trim();
            String durasiText = tfDurasi.getText().trim();

            if (id.isEmpty() || judul.isEmpty() || artis.isEmpty() || genre.isEmpty() || durasiText.isEmpty()) {
                tampilPesan(Alert.AlertType.WARNING, "Peringatan", "Semua data lagu harus diisi.");
                return;
            }

            int durasi = Integer.parseInt(durasiText);
            Song lagu = new Song(id, judul, artis, genre, durasi);
            manager.tambahLagu(lagu);

            bersihkanForm();
            tampilPesan(Alert.AlertType.INFORMATION, "Berhasil", "Berhasil menambahkan lagu.");
        } catch (NumberFormatException e) {
            tampilPesan(Alert.AlertType.ERROR, "Kesalahan", "Durasi lagu harus berupa angka.");
        }
    }

    private void hapusLagu() {
        Song laguDipilih = tabel.getSelectionModel().getSelectedItem();

        if (laguDipilih != null) {
            manager.hapusLagu(laguDipilih);
            tampilPesan(Alert.AlertType.INFORMATION, "Berhasil", "Berhasil menghapus lagu.");
        } else {
            tampilPesan(Alert.AlertType.WARNING, "Peringatan", "Pilih lagu yang ingin dihapus.");
        }
    }

    private void cariLagu() {
        String kataKunci = tfCari.getText();
        ObservableList<Song> hasil = manager.cariLagu(kataKunci);
        tabel.setItems(hasil);
    }

    private void bersihkanForm() {
        tfId.clear();
        tfJudul.clear();
        tfArtis.clear();
        tfGenre.clear();
        tfDurasi.clear();
    }

    private void isiDataContoh() {
        manager.tambahLagu(new Song("0097", "Pelangi Di Matamu", "Jamrud", "Rock", 5));
        manager.tambahLagu(new Song("0098", "Hati-Hati di Jalan", "Tulus", "Pop", 4));
        manager.tambahLagu(new Song("0099", "Zona Nyaman", "Fourtwnty", "Indie", 4));
        manager.tambahLagu(new Song("1000", "Kuning", "Rumahsakit", "Pop", 4));
        manager.tambahLagu(new Song("1001", "Monokrom", "Tulus", "Pop", 4));
        manager.tambahLagu(new Song("1002", "Interaksi", "Tulus", "Pop", 4));
        manager.tambahLagu(new Song("1003", "Sial", "Mahalini", "Pop", 4));
        manager.tambahLagu(new Song("1004", "Melukis Senja", "Budi Doremi", "Pop", 5));
        manager.tambahLagu(new Song("1005", "Komang", "Raim Laode", "Pop", 4));
        manager.tambahLagu(new Song("1006", "Tak Ingin Usai", "Keisya Levronka", "Pop", 4));
        manager.tambahLagu(new Song("1007", "Mesin Waktu", "Budi Doremi", "Pop", 4));
        manager.tambahLagu(new Song("1008", "Akad", "Payung Teduh", "Indie", 4));
        manager.tambahLagu(new Song("1009", "Resah", "Payung Teduh", "Indie", 5));
        manager.tambahLagu(new Song("1010", "Untuk Perempuan yang Sedang Dalam Pelukan", "Payung Teduh", "Indie", 5));
        manager.tambahLagu(new Song("1011", "Amin Paling Serius", "Sal Priadi", "Indie", 4));
        manager.tambahLagu(new Song("1012", "Kita ke Sana", "Hindia", "Indie", 4));
        manager.tambahLagu(new Song("1013", "Evaluasi", "Hindia", "Indie", 5));
        manager.tambahLagu(new Song("1014", "Secukupnya", "Hindia", "Indie", 5));
        manager.tambahLagu(new Song("1015", "Rumah ke Rumah", "Hindia", "Indie", 4));
        manager.tambahLagu(new Song("1016", "Bertaut", "Nadin Amizah", "Pop", 5));
        manager.tambahLagu(new Song("1017", "Sorai", "Nadin Amizah", "Pop", 4));
        manager.tambahLagu(new Song("1018", "Rumpang", "Nadin Amizah", "Pop", 5));
        manager.tambahLagu(new Song("1019", "Celengan Rindu", "Fiersa Besari", "Pop", 4));
        manager.tambahLagu(new Song("1020", "April", "Fiersa Besari", "Pop", 4));
        manager.tambahLagu(new Song("1021", "Waktu yang Salah", "Fiersa Besari", "Pop", 5));
        manager.tambahLagu(new Song("1022", "Cinta Luar Biasa", "Andmesh", "Pop", 4));
        manager.tambahLagu(new Song("1023", "Sampai Jadi Debu", "Banda Neira", "Indie", 5));
        manager.tambahLagu(new Song("1024", "Yang Patah Tumbuh, Yang Hilang Berganti", "Banda Neira", "Indie", 5));
    }

    private void tampilPesan(Alert.AlertType type, String judul, String pesan) {
        Alert alert = new Alert(type);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    public Parent getView() {
        return root;
    }
}
