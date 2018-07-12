import javafx.beans.property.SimpleStringProperty;

public class MovementData {
    private SimpleStringProperty yellowMove;
    private SimpleStringProperty blueMove;
    private SimpleStringProperty orangeMove;
    private SimpleStringProperty greenMove;

    public MovementData(){
        this.yellowMove = new SimpleStringProperty("Y");
        this.blueMove = new SimpleStringProperty("B");
        this.orangeMove = new SimpleStringProperty("O");
        this.greenMove = new SimpleStringProperty("G");
    }

    public MovementData(String y, String b, String o, String g){
        this.yellowMove = new SimpleStringProperty(y);
        this.blueMove = new SimpleStringProperty(b);
        this.orangeMove = new SimpleStringProperty(o);
        this.greenMove = new SimpleStringProperty(g);
    }

    public String getYellowMove(){
        return yellowMove.get();
    }

    public void setYellowMove(String yMove){
        yellowMove.set(yMove);
    }

    public String getBlueMove(){
        return blueMove.get();
    }

    public void setBlueMove(String bMove){
        blueMove.set(bMove);
    }

    public String getOrangeMove(){
        return orangeMove.get();
    }

    public void setOrangeMove(String oMove){
        orangeMove.set(oMove);
    }

    public String getGreenMove(){
        return greenMove.get();
    }

    public void setGreenMove(String gMove){
        greenMove.set(gMove);
    }


}
