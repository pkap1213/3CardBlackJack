import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


// Shows server welcome UI and main server state scene

public class ServerMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("server_welcome.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1000, 680);
        scene.getStylesheets().add(getClass().getResource("server.css").toExternalForm());
        primaryStage.setTitle("3 CARD POKER - Server");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}