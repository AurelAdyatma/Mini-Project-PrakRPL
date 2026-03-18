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
        textField.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #D0D7DE;" +
                "-fx-background-color: white;" +
                "-fx-padding: 10;" +
                "-fx-font-size: 13px;"
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
                "-fx-border-color: #DCE3EA;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 8;"
        );
        tabel.setPrefHeight(500);
    }

    private void buatLayout() {
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #F8FAFC, #EAF2FF);");

        Label lblHeader = new Label("MiniProject Aplikasi Manajemen Musik");
        lblHeader.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1E3A5F;"
        );

        Label lblSubHeader = new Label("Kelola data lagu dengan tampilan yang lebih rapi dan mudah digunakan");
        lblSubHeader.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #5B6B7A;"
        );

        VBox headerBox = new VBox(6, lblHeader, lblSubHeader);
        headerBox.setPadding(new Insets(20, 25, 15, 25));

        VBox formCard = new VBox(10);
        formCard.setPadding(new Insets(20));
        formCard.setPrefWidth(330);
        formCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: #DCE3EA;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);"
        );

        Label lblForm = new Label("Form Data Lagu");
        lblForm.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1E3A5F;"
        );

        Label lblFormDesc = new Label("Silakan isi data lagu dengan lengkap");
        lblFormDesc.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #6B7280;"
        );

        Button btnTambah = buatTombol("Tambah Lagu", "#2563EB");
        Button btnHapus = buatTombol("Hapus Lagu", "#DC2626");
        Button btnCari = buatTombol("Cari Lagu", "#059669");
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
        tabelCard.setPadding(new Insets(20));
        tabelCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: #DCE3EA;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 4);"
        );

        Label lblTabel = new Label("Daftar Lagu");
        lblTabel.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1E3A5F;"
        );

        Label lblTabelDesc = new Label("Data lagu yang sudah tersimpan akan tampil di bawah ini");
        lblTabelDesc.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #6B7280;"
        );

        tabelCard.getChildren().addAll(lblTabel, lblTabelDesc, tabel);

        HBox contentBox = new HBox(20, formCard, tabelCard);
        contentBox.setPadding(new Insets(0, 25, 25, 25));

        root.setTop(headerBox);
        root.setCenter(contentBox);
    }

    private Label buatLabelField(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #374151;"
        );
        return label;
    }

    private Button buatTombol(String text, String warna) {
        Button button = new Button(text);
        button.setPrefWidth(135);
        button.setPrefHeight(40);
        button.setStyle(
                "-fx-background-color: " + warna + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
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
        manager.tambahLagu(new Song("1567", "Pelangi Di Matamu", "Jamrud", "Rock", 5));
        manager.tambahLagu(new Song("2234", "Hati-Hati di Jalan", "Tulus", "Pop", 4));
        manager.tambahLagu(new Song("3312", "Zona Nyaman", "Fourtwnty", "Indie", 4));
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
