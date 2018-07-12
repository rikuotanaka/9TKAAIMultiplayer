import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;


public class ServerSide2Controller {
    @FXML Button stopButton;
    @FXML Button resetButton;
    @FXML Label timerText;


    @FXML private void handleStopButton(){
        GameServer.stop();
    }
    @FXML private void handleResetButton(){GameServer.resetGame();}

    @FXML private void initialize(){

        try {
            GameServer.setController(this);
            GameServer.init();
        }
        catch( IOException e){
            e.printStackTrace();
        }
    }

    public void close(){
        Platform.runLater(() ->{
            Stage stage = (Stage) stopButton.getScene().getWindow();
            stage.close();
        });
    }
    public void setTime(long minutes, long seconds){
        Platform.runLater(() -> {
            String sMinute = Long.toString(minutes);
            String sSecond = Long.toString(seconds);

            if(minutes < 10)sMinute = "0"+sMinute;
            if(seconds < 10)sSecond = "0"+sSecond;
            timerText.setText(sMinute+ " : "+ sSecond);
        });
    }
}
