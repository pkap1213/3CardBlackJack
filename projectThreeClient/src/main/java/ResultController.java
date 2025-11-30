import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public class ResultController {

    @FXML private Label resultMsg;
    @FXML private Label amountLabel;
    @FXML private Button playAgainBtn;
    @FXML private Button exitBtn;
    @FXML private VBox rootVBox;

    private GameController gameController;
    private ClientNetworkManager networkManager;

    // Displays game result amount and message
    public void setGameResult(int amount, String message) {
        String sign;
        if (amount >= 0) {
            sign = "+$";
        } else {
            sign = "-$";
        }
        amountLabel.setText(sign + Math.abs(amount));

        String style;
        if (amount >= 0) {
            style = "-fx-text-fill: #00ff00;";
        } else {
            style = "-fx-text-fill: #ff0000;";
        }
        amountLabel.setStyle(style);

        String msg;
        if (message != null) {
            msg = message;
        } else {
            if (amount >= 0) {
                msg = "You Won!";
            } else {
                msg = "You Lost!";
            }
        }
        resultMsg.setText(msg);
    }

    @FXML
    private void initialize() {
        if (rootVBox != null && AppState.isNewLookEnabled()) {
            if (!rootVBox.getStyleClass().contains("new-look")) {
                rootVBox.getStyleClass().add("new-look");
            }
        } else if (rootVBox != null) {
            rootVBox.getStyleClass().remove("new-look");
        }
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    public void setNetworkManager(ClientNetworkManager mgr) {
        this.networkManager = mgr;
    }

    // Handles the "Play Again" button click

    @FXML
    private void onPlayAgain() {
        try {

            networkManager.stopListener();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
            Parent root = loader.load();
            GameController controller = loader.getController();
            
            controller.setNetworkManager(networkManager);
            
            Stage stage = (Stage) playAgainBtn.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 650);
            scene.getStylesheets().add(getClass().getResource("game.css").toExternalForm());
            stage.setScene(scene);
            
            javafx.application.Platform.runLater(() -> {
                controller.resetForNewHand();
                controller.startListening();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onExit() {
        try {
            if (networkManager != null) {
                networkManager.disconnect();
            }
        } catch (Exception ignored) {}
        javafx.application.Platform.exit();
    }
}