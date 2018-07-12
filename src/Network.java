import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class Network {

    //Registers the classes
    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(GameBoardMessage.class);
        kryo.register(MoveMessage.class);
        kryo.register(SetPlayerMessage.class);
        kryo.register(String.class);
        kryo.register(Direction.class);
        kryo.register(GameBoard.class);
        kryo.register(int[][].class);
        kryo.register(int[].class);
        kryo.register(Move.class);
        kryo.register(WinnerMessage.class);
        kryo.register(TimerSkipMessage.class);
        kryo.register(ArrayList.class);
        kryo.register(SimpleStringProperty.class);
        kryo.register(MovementData.class);

    }

    static public class GameBoardMessage{
        public int[][] board;
        public int[] lastMovement;
        public int nextTurn;
        public String phase;
        public int[] points;
        public ArrayList<MovementData> moveData;
    }

    static public class MoveMessage{
        public int row;
        public int col;
        public int counter;
       public Direction dir;
    }

    static public class SetPlayerMessage{
        public int counter;
        public boolean firstTurn;
        public int playerAmount;
    }

    static public class TimerSkipMessage{
        public long minutes;
        public long seconds;
        public int nextTurn;
        public ArrayList<MovementData> moveData;
    }

    static public class WinnerMessage{
        public int[][] board;
        public int winner;
    }

}
