module com.example.chapter3challenge1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.chapter3challenge1 to javafx.fxml;
    exports com.example.chapter3challenge1;
}