import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class WelcomeController {

    @FXML private TextField ipField;
    @FXML private TextField portField;
    @FXML private Button connectBtn;

    private ClientNetworkManager networkManager = new ClientNetworkManager();

    // Handles the Connect button action, has default IP value if left empty
    @FXML
    private void onConnect() {
        
        String host;
        if (ipField.getText().isEmpty()) {
            host = "127.0.0.1";
        } else {
            host = ipField.getText().trim();
        }

        int port = 0;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            connectBtn.setText("Invalid Port");
            return;
        }
        
        boolean ok = networkManager.connect(host, port);
        if (!ok) {
            connectBtn.setText("Connection Failed");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            
            controller.setNetworkManager(networkManager);
            
            Stage stage = (Stage) ((Node) connectBtn).getScene().getWindow();
            Scene scene = new Scene(root, 1100, 650);
            scene.getStylesheets().add(getClass().getResource("game.css").toExternalForm());
            stage.setScene(scene);
            
            javafx.application.Platform.runLater(() -> {
                controller.startListening();
                controller.resetForNewHand();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            connectBtn.setText("Load Error");
        }
    }

    @FXML
    private void onQuit() {
        try {
            if (networkManager != null) {
                networkManager.disconnect();
            }
        } catch (Exception ignored) {}
        javafx.application.Platform.exit();
    }
}