import com.esotericsoftware.kryonet.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientServerLauncher extends Application {
    private Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("Server/FXML/ServerClientLauncher.fxml"));
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("9TKA - Launcher");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e ->{
            if(GameServer.running())
                Platform.runLater(()-> GameServer.stop());
            if(GameClient.running())
                Platform.runLater(() -> GameClient.send("CLOSED"));

        });
        primaryStage.show();

        System.setProperty("java.net.preferIPv4Stack" , "true");

    }
}
