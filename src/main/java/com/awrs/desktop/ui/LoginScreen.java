package com.awrs.desktop.ui;

import com.awrs.desktop.AppContext;
import com.awrs.exception.AuthenticationException;
import com.awrs.model.User;
import com.awrs.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScreen {

    private final Stage stage;

    public LoginScreen(Stage stage) {
        this.stage = stage;
    }

    public Scene build() {
        VBox root = new VBox(14);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("login-root");

        ImageView logo = new ImageView();
        try {
            logo.setImage(new Image(getClass().getResourceAsStream("/static/images/logo.png")));
        } catch (Exception ignored) {
            // logo optional
        }
        logo.setFitWidth(90);
        logo.setFitHeight(90);
        logo.setPreserveRatio(true);

        Label title = new Label("AWRS");
        title.getStyleClass().add("title");
        Label subtitle = new Label("Automated Warehouse Restock System");
        subtitle.getStyleClass().add("subtitle");

        TextField username = new TextField();
        username.setPromptText("Username");
        username.setMaxWidth(320);

        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setMaxWidth(320);

        Label error = new Label();
        error.getStyleClass().add("error");
        error.setVisible(false);
        error.setWrapText(true);
        error.setMaxWidth(320);

        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("primary-button");
        loginBtn.setMaxWidth(320);
        loginBtn.setDefaultButton(true);

        Label hint = new Label("Demo: admin/admin123  |  manager/manager123  |  worker/worker123");
        hint.getStyleClass().add("hint");

        loginBtn.setOnAction(e -> {
            error.setVisible(false);
            try {
                AuthService authService = AppContext.getBean(AuthService.class);
                User user = authService.login(username.getText().trim(), password.getText());
                stage.setScene(new MainScreen(stage, user).build());
            } catch (AuthenticationException | IllegalArgumentException ex) {
                error.setText(ex.getMessage());
                error.setVisible(true);
            }
        });

        root.getChildren().addAll(logo, title, subtitle, username, password, error, loginBtn, hint);
        Scene scene = new Scene(root, 480, 560);
        scene.getStylesheets().add(getClass().getResource("/desktop/app.css").toExternalForm());
        return scene;
    }
}
