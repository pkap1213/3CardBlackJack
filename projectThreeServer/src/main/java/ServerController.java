
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.application.Platform;

// JavaFX controller for server UI.

public class ServerController {

    @FXML private TextField portField;
    @FXML private Button startBtn;
    @FXML private Button stopBtn;
    @FXML private ListView<String> eventsList;
    @FXML private Label connectedCountLabel;

    private ServerNetworkManager networkManager;

    // Starts the server on the specified port
    @FXML
    private void onStartServer() {
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (Exception e) {
            addEvent("Invalid port");
            return;
        }
        networkManager = new ServerNetworkManager(this);
        try {
            networkManager.start(port);
            addEvent("Server started on port " + port);
            startBtn.setDisable(true);
            stopBtn.setDisable(false);
            updateClientCount(0);
        } catch (Exception ex) {
            addEvent("Failed to start server: " + ex.getMessage());
        }
    }

    // Stops the server
    @FXML
    private void onStopServer() {
        if (networkManager != null) {
            networkManager.stop();
            addEvent("Server stopped");
            startBtn.setDisable(false);
            stopBtn.setDisable(true);
            updateClientCount(0);
        }
    }

    // Adds event to later be displayed on the list view
    public void addEvent(String s) {
        javafx.application.Platform.runLater(() -> {
            eventsList.getItems().add(0, java.time.LocalDateTime.now().toString() + " - " + s);
        });
    }

        public void updateClientCount(int count) {
        Platform.runLater(() -> {
            if (connectedCountLabel != null) {
                connectedCountLabel.setText("Clients: " + count);
            }
        });
    }
}