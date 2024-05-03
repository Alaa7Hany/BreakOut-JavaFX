module com.example.breakout {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.breakout to javafx.fxml;
    exports com.example.breakout;
}