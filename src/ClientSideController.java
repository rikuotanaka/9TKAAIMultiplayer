import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ClientSideController extends GeneralController {
    @FXML Button startClientButton;
    @FXML TextField portNumText;
    @FXML TextField ipAddressText;
    @FXML TextField nameText;
    //GameClient client;

    @FXML private void handleStartClientButton() throws Exception {
        changeScene(startClientButton,"Server/FXML/ClientSide2.fxml","Client Game", 550, 682);
        int portNum = Integer.parseInt(portNumText.getText());
        String ipAddress = ipAddressText.getText();
        String name = nameText.getText();
        GameClient.connect(name, ipAddress, portNum);

        //Set some values
    }

}
