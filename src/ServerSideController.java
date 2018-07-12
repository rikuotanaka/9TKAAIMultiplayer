import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class ServerSideController extends  GeneralController{

    private int numOfPlayers = 2;
    @FXML ChoiceBox modeChoice;
    @FXML Button startButton;
    @FXML TextField portNumText;
    @FXML TextField timeMinutes;
    @FXML TextField timeSeconds;

    @FXML private void handleModeChoice() {
        numOfPlayers = 2 + modeChoice.getSelectionModel().getSelectedIndex();
        System.out.println(numOfPlayers +" players");
    }
    @FXML private void handleStartButton(){
        try {

            int portNum = Integer.parseInt(portNumText.getText());
            long timeMilliseconds = Integer.parseInt(timeSeconds.getText()) * 1000;
            timeMilliseconds += Integer.parseInt(timeMinutes.getText()) * 60 * 1000;
            //System.out.println(numOfPlayers +" players before init server");
            if(timeMilliseconds > 0)
                GameServer.setTimeMilliseconds(timeMilliseconds);
            GameServer.setPlayerAmount(numOfPlayers);
            GameServer.setPortNum(portNum);
            changeScene(startButton,"Server/FXML/ServerSide2.fxml","SERVER RUNNING");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
