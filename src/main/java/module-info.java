module com.marinescu.fxmoney {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires com.jfoenix;
    requires pdfbox.app;
    requires java.desktop;
    requires annotations;

    opens com.marinescu.fxmoney to javafx.fxml;
    opens com.marinescu.fxmoney.Model to javafx.fxml;
    exports com.marinescu.fxmoney.Model;
    exports com.marinescu.fxmoney;
}