import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ClientGameController {

    //@FXML Button resetGameButton;
    @FXML Label curTurnLabel;
    @FXML Label curPhaseLabel;

    @FXML Label playerNumLabel1;
    @FXML Label playerNumLabel2;
    @FXML Label playerNumLabel3;
    @FXML Label playerNumLabel4;

    @FXML Label totalTextLabel1;
    @FXML Label totalTextLabel2;

    @FXML Label playerPointLabel1;
    @FXML Label playerPointLabel2;
    @FXML Label playerPointLabel3;
    @FXML Label playerPointLabel4;

    @FXML Label totalPointLabel1;
    @FXML Label totalPointLabel2;


    @FXML BorderPane canvas;

    @FXML Label timeLeftText;

    private GameBoard board;
    private DisplayedBoard display;
    private MovementHistory history;
    private int playerAmount;
    private String turnText[];

    @FXML
    private void initialize() {
        turnText = new String[]{"Player 1 - Yellow", "Player 2 - Blue", "Player 3 - Orange", "Player 4 - Green"};
        board = new GameBoard(2,new boolean[]{true, true});
        display = new DisplayedBoard(board);

        canvas.setCenter(display);
        GameClient.setDisplay(display);
        GameClient.setGameController(this);
        //Stage stage = (Stage) resetGameButton.getScene().getWindow();
    }
    public void gameOver(String s){
        Platform.runLater(()-> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("GAME OVER");
            //alert.setContentText("");
            alert.setHeaderText(s);
            alert.show();
        });

    }
    public void setup(int playerAmount){

        if(playerAmount != 4){
            totalTextLabel1.setVisible(false);
            totalTextLabel2.setVisible(false);

            totalPointLabel1.setVisible(false);
            totalPointLabel2.setVisible(false);
        }

        if(playerAmount == 2){
            playerNumLabel2.setVisible(false);
            playerNumLabel4.setVisible(false);
            playerPointLabel2.setVisible(false);
            playerPointLabel4.setVisible(false);
            Platform.runLater(()->{
                playerNumLabel3.setText("Player 2");
            });

        }
        else if (playerAmount == 3){
            playerNumLabel4.setVisible(false);
            playerPointLabel4.setVisible(false);
        }
        this.playerAmount = playerAmount;

        if(playerAmount == 4){
            history = new MovementHistory(playerAmount);
            Platform.runLater(()->{
                canvas.setRight(history);
                Stage stage = (Stage) playerNumLabel1.getScene().getWindow();
                stage.setWidth(950);
            });
        }

    }
    public void updateInfo(int turn, String phase, int[]points, ArrayList<MovementData> data) {

        Platform.runLater(()->{
            //Update Current Phase and Turn
            curPhaseLabel.setText(phase);
            curTurnLabel.setText(turnText[turn - 1]);

            //System.out.println(points);
            //Update Points
            if(playerAmount == 2){
                playerPointLabel1.setText(points[0] + " point(s)");
                playerPointLabel3.setText(points[1] + " point(s)");
            }
            else if(playerAmount == 3){
                playerPointLabel1.setText(points[0] + " point(s)");
                playerPointLabel2.setText(points[1] + " point(s)");
                playerPointLabel3.setText(points[2] + " point(s)");
            }
            else if (playerAmount == 4){
                int teamPoints[] = new int[]{points[0] + points[2], points[1] + points[3]};

                playerPointLabel1.setText(points[0] + " point(s)");
                playerPointLabel2.setText(points[1] + " point(s)");
                playerPointLabel3.setText(points[2] + " point(s)");
                playerPointLabel4.setText(points[3] + " point(s)");

                totalPointLabel1.setText(teamPoints[0] + "point(s)");
                totalPointLabel2.setText(teamPoints[1] + "point(s)");

                ObservableList<MovementData> oData = FXCollections.observableArrayList();
                oData.addAll(data);
//                System.out.println("SOURCE DATA: ");
//                for(MovementData m: data) {
//                    System.out.print(m.getYellowMove() + " "+m.getBlueMove() + " "+ m.getOrangeMove() +" "+m.getGreenMove());
//                    System.out.println();
//                }
//                System.out.println("TRANSFERRED DATA: ");
//                for(MovementData m: oData){
//                    System.out.print(m.getYellowMove() + " "+m.getBlueMove() + " "+ m.getOrangeMove() +" "+m.getGreenMove());
//                    System.out.println();
//                }
                history.setItems(oData);
            }
        });
    }

    public void updateInfo(int turn, ArrayList<MovementData> data){
        Platform.runLater(()->{
            curTurnLabel.setText(turnText[turn - 1]);
            ObservableList<MovementData> oData = FXCollections.observableArrayList();
            oData.addAll(data);
            history.setItems(oData);

        });
    }

    public void updateTimeLeft(long minutes, long seconds){
        Platform.runLater(() -> {
            String sMinute = Long.toString(minutes);
            String sSecond = Long.toString(seconds);

            if(minutes < 10)sMinute = "0"+sMinute;
            if(seconds < 10)sSecond = "0"+sSecond;
            timeLeftText.setText("Time Remaining: "+sMinute+ " : "+ sSecond);
        });
    }
    public void close(){
        Platform.runLater(()->{
            Stage stage = (Stage)curPhaseLabel.getScene().getWindow();
            stage.close();    
        });

    }
}
