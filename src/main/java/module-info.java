module com.conmuaxadan.rhythm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;



    exports com.conmuaxadan.rhythm.view;
    opens com.conmuaxadan.rhythm.view to javafx.fxml;
    exports com.conmuaxadan.rhythm.controller;
    opens com.conmuaxadan.rhythm.controller to javafx.fxml;
}