package manajemenmusik;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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
        tfId = new TextField();
        tfJudul = new TextField();
        tfArtis = new TextField();
        tfGenre = new TextField();
        tfDurasi = new TextField();
        tfCari = new TextField();

        tfId.setPromptText("Masukkan ID lagu");
        tfJudul.setPromptText("Masukkan judul lagu");
        tfArtis.setPromptText("Masukkan nama artis");
        tfGenre.setPromptText("Masukkan genre");
        tfDurasi.setPromptText("Masukkan durasi");
        tfCari.setPromptText("Cari judul / artis / genre");
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
    }

    private void buatLayout() {
        VBox kiri = new VBox(10);
        kiri.setPadding(new Insets(15));
        kiri.setPrefWidth(280);

        Label lblForm = new Label("Form Data Lagu");
        lblForm.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button btnTambah = new Button("Tambah Lagu");
        Button btnHapus = new Button("Hapus Lagu");
        Button btnCari = new Button("Cari Lagu");
        Button btnTampilSemua = new Button("Tampilkan Semua");

        btnTambah.setMaxWidth(Double.MAX_VALUE);
        btnHapus.setMaxWidth(Double.MAX_VALUE);
        btnCari.setMaxWidth(Double.MAX_VALUE);
        btnTampilSemua.setMaxWidth(Double.MAX_VALUE);

        btnTambah.setOnAction(e -> tambahLagu());
        btnHapus.setOnAction(e -> hapusLagu());
        btnCari.setOnAction(e -> cariLagu());
        btnTampilSemua.setOnAction(e -> tabel.setItems(manager.getDaftarLagu()));

        kiri.getChildren().addAll(
                lblForm,
                new Label("ID Lagu"),
                tfId,
                new Label("Judul Lagu"),
                tfJudul,
                new Label("Artis"),
                tfArtis,
                new Label("Genre"),
                tfGenre,
                new Label("Durasi"),
                tfDurasi,
                btnTambah,
                btnHapus,
                new Separator(),
                new Label("Pencarian"),
                tfCari,
                btnCari,
                btnTampilSemua
        );

        root.setLeft(kiri);
        root.setCenter(tabel);
        BorderPane.setMargin(tabel, new Insets(15));
    }

    private void tambahLagu() {
        try {
            String id = tfId.getText().trim();
            String judul = tfJudul.getText().trim();
            String artis = tfArtis.getText().trim();
            String genre = tfGenre.getText().trim();
            String durasiText = tfDurasi.getText().trim();

            if (id.isEmpty() || judul.isEmpty() || artis.isEmpty() || genre.isEmpty() || durasiText.isEmpty()) {
                tampilPesan(Alert.AlertType.WARNING, "Peringatan", "Semua data harus diisi.");
                return;
            }

            int durasi = Integer.parseInt(durasiText);

            Song lagu = new Song(id, judul, artis, genre, durasi);
            manager.tambahLagu(lagu);

            bersihkanForm();
            tampilPesan(Alert.AlertType.INFORMATION, "Berhasil", "Berhasil menambahkan lagu.");
        } catch (NumberFormatException e) {
            tampilPesan(Alert.AlertType.ERROR, "Error", "Durasi harus berupa angka.");
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
        manager.tambahLagu(new Song("L001", "Hati-Hati di Jalan", "Tulus", "Pop", 4));
        manager.tambahLagu(new Song("L002", "Melukis Senja", "Budi Doremi", "Pop", 5));
        manager.tambahLagu(new Song("L003", "Zona Nyaman", "Fourtwnty", "Indie", 4));
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