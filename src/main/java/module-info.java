module com.example.policine {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.policine.controllers to javafx.fxml;
    exports com.example.policine;
}