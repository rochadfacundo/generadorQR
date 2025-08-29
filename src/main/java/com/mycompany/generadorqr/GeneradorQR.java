package com.mycompany.generadorqr;

import Entidades.QRUtils;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class GeneradorQR extends Application {

    private final TextField txtContenido = new TextField();
    private final Spinner<Integer> spnSize = new Spinner<>(100, 1200, 400, 50);
    private final ImageView preview = new ImageView();
    private BufferedImage ultimoQR;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Generador de QR - JavaFX + ZXing");

        // --- Fila de entrada (texto + tamaño)
        txtContenido.setPromptText("Ingresá texto o URL");
        txtContenido.setPrefColumnCount(35);

        HBox fila1 = new HBox(10,
                new Label("Texto/URL:"), txtContenido,
                new Label("Tamaño (px):"), spnSize
        );
        fila1.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(txtContenido, Priority.ALWAYS); // se estira con la ventana

        // --- Botones
        Button btnGenerar = new Button("Generar QR");
        Button btnGuardar  = new Button("Guardar PNG");
        btnGuardar.setDisable(true);

        HBox fila2 = new HBox(10, btnGenerar, btnGuardar);
        fila2.setAlignment(Pos.CENTER_LEFT);

        // --- Preview con scroll
        preview.setPreserveRatio(true);

        BorderPane marco = new BorderPane(preview);
        marco.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;");
        marco.setPadding(new Insets(10));

        ScrollPane scroll = new ScrollPane(marco);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setPannable(true);

        // la vista previa se adapta al ancho visible (menos márgenes)
        preview.fitWidthProperty().bind(scroll.widthProperty().subtract(40));

        // --- Root
        VBox root = new VBox(12, fila1, fila2, new Label("Vista previa:"), scroll);
        root.setPadding(new Insets(16));
        root.setPrefWidth(820);

        // --- Eventos
        btnGenerar.setOnAction(e -> generarQR());
        btnGuardar.setOnAction(e -> guardarPNG(stage));
        preview.imageProperty().addListener((o, oldV, newV) -> btnGuardar.setDisable(newV == null));

        // --- Escena y límites
        Scene scene = new Scene(root, 820, 640);
        stage.setScene(scene);
        stage.setMinWidth(720);
        stage.setMinHeight(520);
        stage.show();
    }

    private void generarQR() {
        String contenido = txtContenido.getText().trim();
        if (contenido.isEmpty()) {
            alerta(Alert.AlertType.WARNING, "Falta contenido", "Ingresá un texto o URL para generar el QR.");
            return;
        }
        int size = spnSize.getValue();

        try {
            // Genera BufferedImage grande (para guardar con buena calidad)
            ultimoQR = QRUtils.generarQRImage(contenido, size);

            // Convierte a Image para previsualizar (se escala por fitWidth)
            Image fxImg = SwingFXUtils.toFXImage(ultimoQR, null);
            preview.setImage(fxImg);

        } catch (Exception ex) {
            ex.printStackTrace();
            alerta(Alert.AlertType.ERROR, "Error al generar", ex.getMessage());
        }
    }

  private void guardarPNG(Stage stage) {
    if (ultimoQR == null) {
        alerta(Alert.AlertType.INFORMATION, "Nada para guardar", "Primero generá el QR.");
        return;
    }

    try {
        // 📂 Carpeta "Mis Documentos/QrGenerados"
        String userHome = System.getProperty("user.home");
        File documentos = new File(userHome, "Documents");
        File carpetaQR = new File(documentos, "Qr generados");

        if (!carpetaQR.exists()) {
            carpetaQR.mkdirs(); // crea carpeta si no existe
        }

        // 📝 Nombre del archivo: qr_yyyyMMdd_HHmmss.png
        String nombre = "qr_" + java.time.LocalDateTime.now()
                .toString()
                .replace(":", "-")
                .replace(".", "-") + ".png";

        File destino = new File(carpetaQR, nombre);

        // 🖼️ Guardar imagen
        ImageIO.write(ultimoQR, "PNG", destino);

        alerta(Alert.AlertType.INFORMATION, "Listo",
                "QR guardado en:\n" + destino.getAbsolutePath());

    } catch (Exception ex) {
        ex.printStackTrace();
        alerta(Alert.AlertType.ERROR, "No se pudo guardar", ex.getMessage());
    }
}


    private void alerta(Alert.AlertType type, String titulo, String msg) {
        Alert a = new Alert(type);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
