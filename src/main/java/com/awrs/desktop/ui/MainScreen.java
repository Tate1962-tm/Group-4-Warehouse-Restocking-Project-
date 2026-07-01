package com.awrs.desktop.ui;

import com.awrs.api.dto.DashboardResponse;
import com.awrs.desktop.AppContext;
import com.awrs.model.InventoryRecord;
import com.awrs.model.Item;
import com.awrs.model.RestockTask;
import com.awrs.model.Role;
import com.awrs.model.User;
import com.awrs.model.WarehouseLocation;
import com.awrs.model.AuditLog;
import com.awrs.service.AuthService;
import com.awrs.service.DashboardService;
import com.awrs.service.InventoryService;
import com.awrs.service.RestockService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class MainScreen {

    private final Stage stage;
    private final User user;

    public MainScreen(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public Scene build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-root");

        HBox top = new HBox(12);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(12, 20, 12, 20));
        top.getStyleClass().add("top-bar");

        ImageView logo = new ImageView();
        try {
            logo.setImage(new Image(getClass().getResourceAsStream("/static/images/logo.png")));
        } catch (Exception ignored) {
        }
        logo.setFitWidth(36);
        logo.setFitHeight(36);

        Label brand = new Label("AWRS Warehouse System");
        brand.getStyleClass().add("brand");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label(user.getUsername() + " (" + user.getRole() + ")");
        userLabel.getStyleClass().add("user-label");

        Button logout = new Button("Sign Out");
        logout.setOnAction(e -> stage.setScene(new LoginScreen(stage).build()));

        top.getChildren().addAll(logo, brand, spacer, userLabel, logout);
        root.setTop(top);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().add(dashboardTab());
        tabs.getTabs().add(itemsTab());
        tabs.getTabs().add(locationsTab());
        tabs.getTabs().add(receiveTab());
        tabs.getTabs().add(fulfillTab());
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER) {
            tabs.getTabs().add(adjustTab());
        }
        tabs.getTabs().add(restockTab());
        tabs.getTabs().add(auditTab());

        root.setCenter(tabs);
        Scene scene = new Scene(root, 1200, 760);
        scene.getStylesheets().add(getClass().getResource("/desktop/app.css").toExternalForm());
        return scene;
    }

    private Tab dashboardTab() {
        Tab tab = new Tab("Dashboard");
        VBox box = new VBox(12);
        box.setPadding(new Insets(16));
        Label stats = new Label();
        ListView<String> alerts = new ListView<>();
        ListView<String> tasks = new ListView<>();
        Runnable refresh = () -> {
            DashboardService ds = AppContext.getBean(DashboardService.class);
            DashboardResponse d = ds.getDashboard();
            stats.setText(String.format(
                    "Items: %d  |  Locations: %d  |  Low Stock: %d  |  Pending Tasks: %d  |  Completed: %d",
                    d.totalItems(), d.totalLocations(), d.lowStockCount(),
                    d.pendingRestockTasks(), d.completedRestockTasks()));
            alerts.setItems(FXCollections.observableArrayList(
                    d.lowStockAlerts().stream()
                            .map(a -> a.itemSku() + " @ " + a.locationId() + " — qty " + a.quantity()
                                    + " (min " + a.minimumThreshold() + ") [" + a.riskLevel() + "]")
                            .toList()));
            tasks.setItems(FXCollections.observableArrayList(
                    d.urgentTasks().stream()
                            .map(t -> t.itemSku() + " @ " + t.locationId() + " — priority " + t.priority()
                                    + " [" + t.status() + "]")
                            .toList()));
        };
        refresh.run();
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refresh.run());
        box.getChildren().addAll(new Label("Overview"), stats, new Label("Low Stock Alerts"), alerts,
                new Label("Urgent Restock Tasks"), tasks, refreshBtn);
        tab.setContent(new ScrollPane(box));
        return tab;
    }

    private Tab itemsTab() {
        Tab tab = new Tab("Items");
        InventoryService inv = AppContext.getBean(InventoryService.class);
        TableView<Item> table = itemTable();
        table.setItems(FXCollections.observableArrayList(inv.listItems()));

        TextField sku = new TextField();
        sku.setPromptText("SKU");
        TextField desc = new TextField();
        desc.setPromptText("Description");
        TextField supplier = new TextField();
        supplier.setPromptText("Supplier");
        Spinner<Integer> min = new Spinner<>(0, 1000, 10);
        Spinner<Integer> reorder = new Spinner<>(0, 1000, 30);

        Button add = new Button("Add Item");
        add.setDisable(!(user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER));
        add.setOnAction(e -> {
            try {
                inv.createItem(new Item(sku.getText(), desc.getText(), supplier.getText(), "EA",
                        min.getValue(), reorder.getValue()));
                table.setItems(FXCollections.observableArrayList(inv.listItems()));
                showInfo("Item added");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        HBox form = new HBox(8, sku, desc, supplier, new Label("Min"), min, new Label("Reorder"), reorder, add);
        form.setPadding(new Insets(12));
        BorderPane pane = new BorderPane(table);
        pane.setBottom(form);
        tab.setContent(pane);
        return tab;
    }

    private Tab locationsTab() {
        Tab tab = new Tab("Locations");
        InventoryService inv = AppContext.getBean(InventoryService.class);
        TableView<WarehouseLocation> table = locationTable();
        table.setItems(FXCollections.observableArrayList(inv.listLocations()));

        TextField id = new TextField();
        id.setPromptText("Location ID");
        TextField name = new TextField();
        name.setPromptText("Name");
        TextField parent = new TextField();
        parent.setPromptText("Parent ID (optional)");

        Button add = new Button("Add Location");
        add.setDisable(!(user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER));
        add.setOnAction(e -> {
            try {
                WarehouseLocation parentLoc = parent.getText().isBlank() ? null
                        : inv.listLocations().stream()
                        .filter(l -> l.getId().equals(parent.getText()))
                        .findFirst().orElse(null);
                inv.createLocation(new WarehouseLocation(id.getText(), name.getText(), "BIN", parentLoc));
                table.setItems(FXCollections.observableArrayList(inv.listLocations()));
                showInfo("Location added");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        HBox form = new HBox(8, id, name, parent, add);
        form.setPadding(new Insets(12));
        BorderPane pane = new BorderPane(table);
        pane.setBottom(form);
        tab.setContent(pane);
        return tab;
    }

    private Tab receiveTab() {
        return transactionTab("Receive Shipment", true);
    }

    private Tab fulfillTab() {
        return transactionTab("Fulfill Order", false);
    }

    private Tab transactionTab(String title, boolean receive) {
        Tab tab = new Tab(title);
        InventoryService inv = AppContext.getBean(InventoryService.class);
        ComboBox<String> itemBox = new ComboBox<>(FXCollections.observableArrayList(
                inv.listItems().stream().map(Item::getSku).toList()));
        ComboBox<String> locBox = new ComboBox<>(FXCollections.observableArrayList(
                inv.listLocations().stream().map(WarehouseLocation::getId).toList()));
        Spinner<Integer> qty = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 10));
        Button action = new Button(receive ? "Receive" : "Fulfill");
        Label result = new Label();

        action.setOnAction(e -> {
            try {
                InventoryRecord record = receive
                        ? inv.receiveShipment(itemBox.getValue(), locBox.getValue(), qty.getValue(), user.getUsername())
                        : inv.fulfillOrder(itemBox.getValue(), locBox.getValue(), qty.getValue(), user.getUsername());
                result.setText("New quantity: " + record.getQuantity());
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        VBox box = new VBox(10, new Label("Item"), itemBox, new Label("Location"), locBox,
                new Label("Quantity"), qty, action, result);
        box.setPadding(new Insets(16));
        tab.setContent(box);
        return tab;
    }

    private Tab adjustTab() {
        Tab tab = new Tab("Adjustments");
        InventoryService inv = AppContext.getBean(InventoryService.class);
        ComboBox<String> itemBox = new ComboBox<>(FXCollections.observableArrayList(
                inv.listItems().stream().map(Item::getSku).toList()));
        ComboBox<String> locBox = new ComboBox<>(FXCollections.observableArrayList(
                inv.listLocations().stream().map(WarehouseLocation::getId).toList()));
        Spinner<Integer> qty = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0));
        TextField reason = new TextField();
        reason.setPromptText("Reason");
        Button action = new Button("Submit Adjustment");
        Label result = new Label();

        action.setOnAction(e -> {
            try {
                InventoryRecord record = inv.adjustInventory(user, itemBox.getValue(), locBox.getValue(),
                        qty.getValue(), reason.getText());
                result.setText("New quantity: " + record.getQuantity());
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        VBox box = new VBox(10, new Label("Item"), itemBox, new Label("Location"), locBox,
                new Label("New Quantity"), qty, new Label("Reason"), reason, action, result);
        box.setPadding(new Insets(16));
        tab.setContent(box);
        return tab;
    }

    private Tab restockTab() {
        Tab tab = new Tab("Restock Tasks");
        RestockService rs = AppContext.getBean(RestockService.class);
        TableView<RestockTask> table = restockTable();
        Runnable refresh = () -> table.setItems(FXCollections.observableArrayList(rs.listTasks()));
        refresh.run();

        Button batch = new Button("Batch Evaluate");
        batch.setOnAction(e -> {
            List<RestockTask> created = rs.batchEvaluate();
            refresh.run();
            showInfo("Created " + created.size() + " task(s)");
        });

        Button complete = new Button("Complete Selected");
        complete.setOnAction(e -> {
            RestockTask selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Select a task first");
                return;
            }
            try {
                rs.completeTask(selected.getId(), user.getUsername());
                refresh.run();
                showInfo("Task completed");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        HBox actions = new HBox(8, batch, complete);
        actions.setPadding(new Insets(12));
        BorderPane pane = new BorderPane(table);
        pane.setBottom(actions);
        tab.setContent(pane);
        return tab;
    }

    private Tab auditTab() {
        Tab tab = new Tab("Audit Log");
        InventoryService inv = AppContext.getBean(InventoryService.class);
        TableView<AuditLog> table = auditTable();
        table.setItems(FXCollections.observableArrayList(inv.listAuditLogs()));
        tab.setContent(table);
        return tab;
    }

    private TableView<Item> itemTable() {
        TableView<Item> table = new TableView<>();
        TableColumn<Item, String> sku = new TableColumn<>("SKU");
        sku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        TableColumn<Item, String> desc = new TableColumn<>("Description");
        desc.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Item, Integer> min = new TableColumn<>("Min");
        min.setCellValueFactory(new PropertyValueFactory<>("minimumThreshold"));
        table.getColumns().addAll(sku, desc, min);
        return table;
    }

    private TableView<WarehouseLocation> locationTable() {
        TableView<WarehouseLocation> table = new TableView<>();
        TableColumn<WarehouseLocation, String> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<WarehouseLocation, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<WarehouseLocation, String> path = new TableColumn<>("Path");
        path.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFullPath()));
        table.getColumns().addAll(id, name, path);
        return table;
    }

    private TableView<RestockTask> restockTable() {
        TableView<RestockTask> table = new TableView<>();
        TableColumn<RestockTask, String> item = new TableColumn<>("Item");
        item.setCellValueFactory(new PropertyValueFactory<>("itemSku"));
        TableColumn<RestockTask, String> loc = new TableColumn<>("Location");
        loc.setCellValueFactory(new PropertyValueFactory<>("locationId"));
        TableColumn<RestockTask, Integer> pri = new TableColumn<>("Priority");
        pri.setCellValueFactory(new PropertyValueFactory<>("priority"));
        TableColumn<RestockTask, String> status = new TableColumn<>("Status");
        status.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getStatus().name()));
        table.getColumns().addAll(item, loc, pri, status);
        return table;
    }

    private TableView<AuditLog> auditTable() {
        TableView<AuditLog> table = new TableView<>();
        TableColumn<AuditLog, String> action = new TableColumn<>("Action");
        action.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAction().name()));
        TableColumn<AuditLog, String> item = new TableColumn<>("Item");
        item.setCellValueFactory(new PropertyValueFactory<>("itemSku"));
        TableColumn<AuditLog, Integer> change = new TableColumn<>("Change");
        change.setCellValueFactory(new PropertyValueFactory<>("quantityChange"));
        TableColumn<AuditLog, String> by = new TableColumn<>("By");
        by.setCellValueFactory(new PropertyValueFactory<>("performedBy"));
        table.getColumns().addAll(action, item, change, by);
        return table;
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, javafx.scene.control.ButtonType.OK);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, javafx.scene.control.ButtonType.OK);
        alert.showAndWait();
    }
}
