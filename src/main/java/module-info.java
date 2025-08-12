module com.example.policine {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.policine.controllers to javafx.fxml;
    exports com.example.policine;
}