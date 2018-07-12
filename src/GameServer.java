import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class GameServer {

    private static Server server;
    private static int portNum;
    private static GameBoard board;
    private static ServerSide2Controller ss;
    private static int playerAmount;
    private static int round;
    private static int playerCounter = 1;
    private static long timeMilliseconds;
    private static long targetTime;
    private static boolean isRunning;

    static
    {
        server = new Server();


    }


    public static void init() throws IOException
    {
        server.start();
        server.bind(portNum);
        board = new GameBoard(playerAmount,new boolean[]{true,true,true,true});
        round = 1;

        //Register Classes
        Network.register(server);
        server.addListener(new Listener(){
            @Override
            public void received(Connection c, Object o) {
                if(o instanceof Network.MoveMessage) {
                    Network.GameBoardMessage gbm;
                    Network.WinnerMessage w;
                    Network.MoveMessage object = (Network.MoveMessage) o;
                    boolean ok;
                    if(board.currentPhase() == 0) {
                        ok = board.placeCounter(object.row, object.col, object.counter);
                    }
                    else {
                        ok = board.moveCounter(object.row, object.col, object.counter);
                    }

                    if(ok) {
                        if(board.isAnyMovementPossible()){
                            targetTime = System.currentTimeMillis() + timeMilliseconds;
                            long timeRemaining = targetTime - System.currentTimeMillis();
                            long secondsRemaining = timeRemaining / 1000;
                            long secondsDisplay = secondsRemaining % 60;
                            long minutesRemaining = secondsRemaining / 60;
                            ss.setTime(minutesRemaining, secondsDisplay);

                            gbm = new Network.GameBoardMessage();
                            gbm.board = board.getBoard();
                            gbm.lastMovement = new int[]{object.row, object.col};
                            gbm.nextTurn = board.getNextTurn();
                            System.out.println("NEXT TURN: "+gbm.nextTurn);
                            gbm.phase = (board.currentPhase() == 0)? "Placement" : "Movement";
                            gbm.points = board.getPoints();
                            gbm.moveData = board.getMovementData();
                            //System.out.println(gbm.points[0]);
                            server.sendToAllTCP(gbm);
                        }
                        else{
                            w = new Network.WinnerMessage();
                            w.board = board.getBoard();
                            w.winner = board.getWinner();
                            server.sendToAllTCP(w);
                        }

                    }

                }

                if(o instanceof String){
                    String text = (String)o;
                    if(text.equals("INIT BOARD")) {

                        Network.GameBoardMessage gbm = new Network.GameBoardMessage();
                        gbm.board = board.getBoard();
                        gbm.lastMovement = null;
                        gbm.nextTurn = 1;
                        gbm.phase = "Placement";
                        gbm.points = board.getPoints();
                        gbm.moveData = board.getMovementData();

                        //System.out.println(gbm.points[0]);
                        server.sendToAllTCP(gbm);
                        if (timeMilliseconds > 0) {
                            targetTime = System.currentTimeMillis() + timeMilliseconds;
                            initTimer();
                        }
                    }
                    else if(text.equals("CLOSED")){
                        stop();
                        Platform.runLater(()->{
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("PLAYER DISCONNECTED");
                            String player = "not a";
                            if(c.getID() == 1)player = "YELLOW";
                            else if(c.getID() == 2)player = "BLUE";
                            else if(c.getID() == 3)player = "Orange";
                            else if(c.getID() == 4)player = "Green";
                            alert.setHeaderText("PLAYER "+ player + " DISCONNECTED!");
                            alert.show();
                        });
                    }
                }
            }

            @Override
            public void connected(Connection connection) {
                System.out.println("NEW CLIENT CONNECTED!");
                if(connection.getID() > playerAmount) {
                    server.sendToTCP(connection.getID(),"NOT ALLOWED");
                    return;
                }
                Network.SetPlayerMessage p = new Network.SetPlayerMessage();
                p.counter = playerCounter;
                p.firstTurn = false;
                p.playerAmount = playerAmount;
                connection.sendTCP(p);
                playerCounter ++;
            }

            @Override
            public void disconnected(Connection c){

            }


        });
    isRunning = true;


    }


    public static void initTimer(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(!board.isAnyMovementPossible())this.cancel();
                long timeRemaining = targetTime - System.currentTimeMillis();
                Network.TimerSkipMessage ts = new Network.TimerSkipMessage();
                if (timeRemaining <= 0) {
                    System.out.println("RESET TIMER!");
                    targetTime = System.currentTimeMillis() + timeMilliseconds;
                    timeRemaining = targetTime - System.currentTimeMillis();
                    board.skip1Turn();
                    ts.nextTurn = board.getNextTurn();
                }
                else{
                    ts.nextTurn = -1;
                }


                long secondsRemaining = timeRemaining / 1000;
                long secondsDisplay = secondsRemaining % 60;
                long minutesRemaining = secondsRemaining / 60;
                ss.setTime(minutesRemaining, secondsDisplay);
                ts.minutes = minutesRemaining;
                ts.seconds = secondsDisplay;
                ts.moveData = board.getMovementData();


                server.sendToAllTCP(ts);
                //System.out.println("TIMER UPDATE!");
            }
        },0, 200);
    }
    public static boolean running(){
        return isRunning;
    }
    public static void setPlayerAmount(int x){
        playerAmount = x;
    }

    public static void setController(ServerSide2Controller ssc){
        ss = ssc;

    }
    public static void setPortNum(int pN){
        portNum = pN;
    }

    public static void setTimeMilliseconds(long time){timeMilliseconds = time; }

    public static void resetGame(){
        //Reset Game Board
        board.resetBoard();

        if(playerAmount == 4) {
            //Set all the players for the next round
            round = (round == 1) ? 2 : 1;

            for (int i = 1; i <= 4 ; i++){
                Network.SetPlayerMessage spm = new Network.SetPlayerMessage();
                int x;
                spm.playerAmount = 4;

                //Set First Turn
                if((i == 1 && round == 1) || (i == 2 && round == 2))
                    spm.firstTurn = true;
                else
                    spm.firstTurn = false;

                //Set Player Counter
                if(round == 1)x = i;
                else {
                    if(i == 1) x = 2;
                    else if(i == 2) x = 1;
                    else if (i == 3) x = 4;
                    else x = 3;
                }

                spm.counter = x;
                server.sendToTCP(i, spm);

            }
        }
        else {
            //Send board information without changing players.
            Network.GameBoardMessage gbm = new Network.GameBoardMessage();
            gbm.board = board.getBoard();
            gbm.lastMovement = null;
            gbm.nextTurn = 1;
            gbm.phase = "Placement";
            gbm.points = board.getPoints();

            //Start Timer
            if (timeMilliseconds > 0) {
                targetTime = System.currentTimeMillis() + timeMilliseconds;
                initTimer();
            }

            server.sendToAllTCP(gbm);
        }
    }

    public static void stop(){
        server.sendToAllTCP("CLOSE");
        server.stop();
        ss.close();
    }
}