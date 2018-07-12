import com.esotericsoftware.kryonet.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ClientServerController extends GeneralController {
    @FXML Button startServerButton;
    @FXML protected void handleStartServerButton() throws Exception {
        changeScene(startServerButton,"Server/FXML/ServerSide.fxml","9TKA - Server Side");
    }

    @FXML protected void handleStartClientButton() throws Exception {
        changeScene(startServerButton,"Server/FXML/ClientSide.fxml", "9TKA - Client Side");
    }
}
