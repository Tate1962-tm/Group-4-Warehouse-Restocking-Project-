package com.awrs.desktop;

import com.awrs.AwrsApplication;
import com.awrs.desktop.ui.LoginScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class AwrsFxApplication extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(AwrsApplication.class)
                .headless(false)
                .properties("spring.main.web-application-type=none")
                .run();
        AppContext.init(springContext);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("AWRS — Automated Warehouse Restock System");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setScene(new LoginScreen(stage).build());
        stage.show();
    }

    @Override
    public void stop() {
        if (springContext != null) {
            springContext.close();
        }
        Platform.exit();
    }
}
