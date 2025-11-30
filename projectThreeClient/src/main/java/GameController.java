import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// GameController - handles UI updates and receives PokerInfo messages from server.

public class GameController {

    @FXML private BorderPane rootPane; 
    @FXML private HBox dealerCardsBox;
    @FXML private HBox playerCardsBox;
    @FXML private TextField anteField;
    @FXML private TextField pairPlusField;
    @FXML private Button dealBtn;
    @FXML private Button playBtn;
    @FXML private Button foldBtn;
    @FXML private Label totalWinnings;
    @FXML private ListView<String> infoList;

    @FXML private Label playWagerValue;
    @FXML private Label pairPlusWagerValue;
    @FXML private Label anteWagerValue;


    @FXML private Button optionsBtn;
    @FXML private MenuItem exitMenuItem;
    @FXML private MenuItem freshStartMenuItem;
    @FXML private MenuItem newLookMenuItem;
    
    @FXML private Pane optionsFrame;
    @FXML private AnchorPane optionsPanelOverlay; 
    @FXML private StackPane rootStack;

    private ClientNetworkManager networkManager;
    private int totalBalance = 0;

    private UUID currentSessionId = null;
    private boolean handActive = false;
    private PokerInfo lastResult;




    // Sets up various aspects of the game scene
    @FXML
    private void initialize() {

        if (optionsFrame != null) {
            optionsFrame.setVisible(false);
            optionsFrame.setManaged(false);
        }

        if (rootStack != null && AppState.isNewLookEnabled()) {
            if (!rootStack.getStyleClass().contains("new-look")) {
                rootStack.getStyleClass().add("new-look");
            }
        } else if (rootStack != null) {
            rootStack.getStyleClass().remove("new-look");
        }

        List<String> preserved = AppState.getPreservedInfo();
        if (infoList != null && !preserved.isEmpty()) {
            infoList.getItems().setAll(preserved);
        }

        totalBalance = AppState.getTotalBalance();
        if (totalWinnings != null) {
            totalWinnings.setText("$" + totalBalance);
        }

        if (anteWagerValue != null){
            anteWagerValue.setText("$0");
        }
        if (pairPlusWagerValue != null){
            pairPlusWagerValue.setText("$0");
        }
        if (playWagerValue != null){
            playWagerValue.setText("$0");
        }
    }

    @FXML
    private void onOptionsToggle() {
        if (optionsFrame == null) return;
        boolean show = !optionsFrame.isVisible();
        optionsFrame.setVisible(show);
        if (show) {
            Platform.runLater(() -> optionsFrame.toFront());
        }
    }

    public void setNetworkManager(ClientNetworkManager mgr) {
        this.networkManager = mgr;
    }

    public void startListening() {
        if (networkManager != null) {
            networkManager.startListener(this::onServerPokerInfo);
        }
    }

    // Handles deal actions like validating wages and sending to server

    @FXML
    private void onDeal() {
        int ante = parseIntOrZero(anteField.getText());
        int pair = parseIntOrZero(pairPlusField.getText());
        
        if (ante < 5 || ante > 25) {
            infoList.getItems().add("- Ante must be $5-$25");
            return;
        }
        
        if (pair > 0 && (pair < 5 || pair > 25)) {
            infoList.getItems().add("- Pair Plus must be $5-$25 or $0");
            return;
        }
        
        PokerInfo info = new PokerInfo();
        info.setAnteBet(ante);
        info.setPairPlusBet(pair);
        networkManager.send(info);

        String msg = "- Bets placed: Ante $" + ante;
        if (pair > 0) {
            msg = msg + " | Pair Plus $" + pair;
        }
        infoList.getItems().add(msg);
        
        if (anteWagerValue != null){
            anteWagerValue.setText("$" + ante);
        }
        if (pairPlusWagerValue != null){
            pairPlusWagerValue.setText("$" + pair);
        }
        if (playWagerValue != null){
            playWagerValue.setText("$0");
        }
        
        dealBtn.setDisable(true);
        playBtn.setDisable(false);
        foldBtn.setDisable(false);
        anteField.setDisable(true);
        pairPlusField.setDisable(true);
    }

    // Handles play actions like sending a play bet to server
    @FXML
    private void onPlay() {
        int ante = parseIntOrZero(anteField.getText());
        PokerInfo info = new PokerInfo();
        info.setPlayBet(ante);
        info.setFold(false);
        networkManager.send(info);
        infoList.getItems().add("- Play wager placed: $" + ante);
        playBtn.setDisable(true);
        foldBtn.setDisable(true);
        
        if (playWagerValue != null){
            playWagerValue.setText("$" + ante);
        }
    }

    // Handles fold bet actions and updates ui
    @FXML
    private void onFold() {
        PokerInfo info = new PokerInfo();
        info.setFold(true);
        networkManager.send(info);
        infoList.getItems().add("- You folded - Ante and Pair Plus lost");
        playBtn.setDisable(true);
        foldBtn.setDisable(true);
    }

    private void onServerPokerInfo(PokerInfo info) {
        javafx.application.Platform.runLater(() -> handleServerResponse(info));
    }


    // Handles server responses
    private void handleServerResponse(PokerInfo info) {
        UUID sid = info.getSessionId();
        boolean hasSession = sid != null;
        boolean isNewSession = hasSession && (currentSessionId == null || !currentSessionId.equals(sid));

        if (hasSession && isNewSession) {
            currentSessionId = sid;
            handActive = true;
            if (info.getPlayerCards() != null && !info.getPlayerCards().isEmpty()) {
                displayCards(info);
            }
            
            dealBtn.setDisable(true);
            playBtn.setDisable(false);
            foldBtn.setDisable(false);
            if (info.getMessage() != null && !info.getMessage().isEmpty()) {
                infoList.getItems().add(info.getMessage());
            } else {
                infoList.getItems().add("Hand dealt.");
            }
            return;
        }

        if (hasSession && currentSessionId != null && currentSessionId.equals(sid)) {
            if (info.isRevealDealer()) {
                displayCards(info);
                if (info.getMessage() != null && !info.getMessage().isEmpty()) {
                    infoList.getItems().add(info.getMessage());
                }
                totalBalance += info.getResultAmount();
                totalWinnings.setText("$" + totalBalance);
                AppState.setTotalBalance(totalBalance);
                lastResult = info;
                dealBtn.setDisable(true);
                playBtn.setDisable(true);
                foldBtn.setDisable(true);

                currentSessionId = null;
                handActive = false;

                new Thread(() -> {
                    try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
                    javafx.application.Platform.runLater(this::showResultScreenWithDelay);
                }).start();
                return;
            }

            if (info.getMessage() != null && !info.getMessage().isEmpty()) {
                infoList.getItems().add(info.getMessage());
            }
            return;
        }

        if (!handActive && info.getPlayerCards() != null && !info.getPlayerCards().isEmpty()) {
            displayCards(info);
        }
        if (info.getMessage() != null && !info.getMessage().isEmpty()) {
            infoList.getItems().add(info.getMessage());
        }
    }

    private void showResultScreenWithDelay() {
        showResultScreen(lastResult);
    }

    // Displays the card images to the user
    private void displayCards(PokerInfo info) {
        playerCardsBox.getChildren().clear();
        dealerCardsBox.getChildren().clear();

        if (info.getPlayerCards() != null) {
            for (Card card : info.getPlayerCards()) {
                ImageView iv = createCardImageView(card.getImagePath(), 85, 125);
                playerCardsBox.getChildren().add(iv);
            }
        }

        if (info.getDealerCards() != null) {
            for (Card card : info.getDealerCards()) {
                String cardPath;
                if (info.isRevealDealer()) {
                    cardPath = card.getImagePath();
                } else {
                    cardPath = "/images/cards/back.png";
                }
                ImageView iv = createCardImageView(cardPath, 85, 125);
                dealerCardsBox.getChildren().add(iv);
            }
        }
    }


    @FXML
    private void onExitMenu() {
        try {
            if (networkManager != null) {
                networkManager.disconnect();
            }
        } catch (Exception ignored) {}
        javafx.application.Platform.exit();
    }

    // handles fresh start button press

    @FXML
    private void onFreshStartMenu() {

        totalBalance = 0;
        AppState.setTotalBalance(0);
        totalWinnings.setText("$0");
        AppState.clearPreservedInfo();
        resetForNewHand();
        infoList.getItems().add("Fresh Start: winnings reset to $0");

        if (anteWagerValue != null){
            anteWagerValue.setText("$0");
        }
        if (pairPlusWagerValue != null){
            pairPlusWagerValue.setText("$0");
        }
        if (playWagerValue != null){
            playWagerValue.setText("$0");
        }
    }

    // handles new look button press
    @FXML
    private void onNewLookMenu() {
        if (rootStack == null) return;
        boolean turningOn = !rootStack.getStyleClass().contains("new-look");
        if (turningOn) {
            rootStack.getStyleClass().add("new-look");
            infoList.getItems().add("NewLook: dark green applied");
        } else {
            rootStack.getStyleClass().remove("new-look");
            infoList.getItems().add("NewLook: original look restored");
        }
        AppState.setNewLookEnabled(turningOn);
    }

    private ImageView createCardImageView(String imagePath, double width, double height) {
        try {
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            if (img.isError()) {
                throw new Exception("Image load error: " + imagePath);
            }
            ImageView iv = new ImageView(img);
            iv.setFitWidth(width);
            iv.setFitHeight(height);
            iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) {
            System.err.println("Failed to load image: " + imagePath);
            e.printStackTrace();
            ImageView iv = new ImageView();
            iv.setFitWidth(width);
            iv.setFitHeight(height);
            iv.setStyle("-fx-border-color: #888888; -fx-border-width: 2; -fx-background-color: #cccccc;");
            return iv;
        }
    }

    // shows the result scene to the user

    private void showResultScreen(PokerInfo info) {
        try {
            if (infoList != null) {
                AppState.setPreservedInfo(new ArrayList<>(infoList.getItems()));
            } else {
                AppState.clearPreservedInfo();
            }

            AppState.setTotalBalance(totalBalance);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("result.fxml"));
            Parent root = loader.load();
            ResultController controller = loader.getController();

            int amount = 0;
            String msg = "";
            if (info != null) {
                amount = info.getResultAmount();
                if (info.getMessage() != null) {
                    msg = info.getMessage();
                } else {
                    msg = "";
                }
            }

            controller.setGameResult(amount, msg);
            controller.setGameController(this);
            controller.setNetworkManager(networkManager);
            
            Stage stage = (Stage) dealBtn.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 650);
            scene.getStylesheets().add(getClass().getResource("game.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            infoList.getItems().add("Error loading result screen");
        }
    }

    // resets client side aspects for new game
    public void resetForNewHand() {

        currentSessionId = null;
        handActive = false;

        dealBtn.setDisable(false);
        playBtn.setDisable(true);
        foldBtn.setDisable(true);
        anteField.setDisable(false);
        pairPlusField.setDisable(false);
        anteField.clear();
        pairPlusField.clear();
        
        dealerCardsBox.getChildren().clear();
        playerCardsBox.getChildren().clear();
        
        for (int i = 0; i < 3; i++) {
            dealerCardsBox.getChildren().add(createBackImageView(85, 125));
            playerCardsBox.getChildren().add(createBackImageView(85, 125));
        }

        List<String> preserved = AppState.getPreservedInfo();
        if (preserved != null && !preserved.isEmpty()) {
            infoList.getItems().setAll(preserved);
        } else {
            infoList.getItems().clear();
        }
        totalWinnings.setText("$" + totalBalance);

        if (anteWagerValue != null){
            anteWagerValue.setText("$0");
        }
        if (pairPlusWagerValue != null){
            pairPlusWagerValue.setText("$0");
        }
        if (playWagerValue != null){
            playWagerValue.setText("$0");
        }
    }

    // handles back card image placement
    private ImageView createBackImageView(double width, double height) {
        String backPath = "/images/cards/back.png";
        try {
            Image img = new Image(getClass().getResourceAsStream(backPath));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(width);
            iv.setFitHeight(height);
            iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) {
            // fallback placeholder if image missing
            ImageView iv = new ImageView();
            iv.setFitWidth(width);
            iv.setFitHeight(height);
            iv.setStyle("-fx-border-color: #888888; -fx-border-width: 1; -fx-background-color: #cccccc;");
            return iv;
        }
    }

    // attempt to parse integer passed from string or return 0

    private int parseIntOrZero(String s) {
        try { 
            return Integer.parseInt(s.trim()); 
        } catch (Exception e) { 
            return 0; 
        }
    }
}