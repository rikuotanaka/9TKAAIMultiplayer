import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;

public class GameClient {
    private static Client client;
    private static String clientName;
    private static boolean isRunning;
    private static int[][] gameBoard;
    private static int[] lastCounter;
    private static DisplayedBoard display;
    private static int playerCounter;
    private static ClientGameController cgc;

    static {
        client = new Client();
    }

    public static void connect(String name, String ipAddress, int portNumber) throws IOException
    {
        clientName = name;
        Network.register(client);
        client.addListener(new Listener(){

            @Override
            public void connected(Connection c) {


//                Platform.runLater(()->{
//                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("PLAYER INFO");
//                    String player = "not a";
//                    if(c.getID() == 1)player = "Yellow";
//                    else if(c.getID() == 2)player = "Blue";
//                    else if(c.getID() == 3)player = "Orange";
//                    else if(c.getID() == 4)player = "Green";
//                    alert.setHeaderText("You are "+ player + " player");
//                    alert.show();
//                });

            }

            @Override
            public void received(Connection c, Object o){
                if(o instanceof  String){
                    System.out.println((String)o);
                    if(((String) o).equals("CLOSE")){
                        cgc.close();
                    }
                    else if(o.equals("NOT ALLOWED")){
                        cgc.close();
                        Platform.runLater(() ->{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("ERROR");
                            alert.setHeaderText("You can no longer connect to this server.");
                            alert.setContentText("The Server already has max no. of players");
                        });
                    }
                }

                if(o instanceof  Network.GameBoardMessage) {
                    gameBoard = ((Network.GameBoardMessage) o).board;
                    lastCounter = ((Network.GameBoardMessage) o).lastMovement;
                   // int[] points = ((Network.GameBoardMessage) o).points;
                    if(lastCounter!=null)System.out.println("LAST COUNTER: "+lastCounter[0]+", "+ lastCounter[1]);
                    display.setControl(playerCounter == ((Network.GameBoardMessage) o).nextTurn);
                    display.updateDisplay();
                    cgc.updateInfo(((Network.GameBoardMessage) o).nextTurn,
                            ((Network.GameBoardMessage) o).phase,
                            ((Network.GameBoardMessage) o).points,
                            ((Network.GameBoardMessage) o).moveData);
                   /* if(lastCounter != null && ((Network.GameBoardMessage) o).phase.equals("Placement"))
                        display.animatePlace(lastCounter[0],lastCounter[1]);*/
                }

                if(o instanceof Network.SetPlayerMessage){
                    playerCounter = ((Network.SetPlayerMessage) o).counter;
                    display.setControl(((Network.SetPlayerMessage) o).firstTurn);
                    cgc.setup(((Network.SetPlayerMessage) o).playerAmount);
                    if(c.getID() == ((Network.SetPlayerMessage) o).playerAmount){
                        //Request initial display
                        client.sendTCP("INIT BOARD");

                    }

                    Platform.runLater(()->{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("PLAYER INFO");
                        String player = "not a";
                        if(c.getID() == 1)player = "Yellow";
                        else if(c.getID() == 2)player = "Blue";
                        else if(c.getID() == 3)player = "Orange";
                        else if(c.getID() == 4)player = "Green";
                        alert.setHeaderText("You are "+ player + " player");
                        alert.show();
                    });

                    System.out.println("THIS CLIENT IS PLAYER "+ ((Network.SetPlayerMessage) o).counter);
                    if(((Network.SetPlayerMessage) o).firstTurn)System.out.println("THIS CLIENT GETS TO MOVE FIRST!");
                }
                if(o instanceof  Network.TimerSkipMessage){
                    cgc.updateTimeLeft(((Network.TimerSkipMessage) o).minutes,
                            ((Network.TimerSkipMessage) o).seconds);

                    if(((Network.TimerSkipMessage) o).nextTurn > 0){
                        display.setControl(c.getID() == ((Network.TimerSkipMessage) o).nextTurn);
                        cgc.updateInfo(((Network.TimerSkipMessage) o).nextTurn,
                                ((Network.TimerSkipMessage) o).moveData);
                    }
                }

                if(o instanceof  Network.WinnerMessage){
                    gameBoard = ((Network.WinnerMessage) o).board;
                    display.updateDisplay();
                    display.setControl(false);
                    int winner = ((Network.WinnerMessage) o).winner;
                    if(winner == c.getID())cgc.gameOver("YOU WIN!!!");
                    else cgc.gameOver("YOU LOSE!!!");
                }


            }
        });
        client.start();
        client.connect(5000, ipAddress, portNumber);
        isRunning = true;
    }
    public static void send(Object o){
        client.sendTCP(o);
    }
    public static int[][] getGameBoard(){
        return gameBoard;
    }
    public static int getPlayerCounter(){return playerCounter;}
    public static int[] getLastCounter(){return lastCounter;}
    public static void setDisplay(DisplayedBoard dp){
        display = dp;
    }
    public static void setGameController(ClientGameController cg){
        cgc = cg;
    }
    public static boolean running(){return isRunning;};

}
