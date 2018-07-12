import javafx.animation.FillTransition;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class DisplayedBoard extends Pane  implements EventHandler<MouseEvent>{

    private int boardWidth = 528;
    private int boardHeight = 528;
    private Color[] tileColor = new Color[]{new Color(0.9804, 0.9608, 0.6784, 1),
            new Color(0.7647, 1, 0.702, 1)};
    private Color[] counterColor = new Color[]{Color. YELLOW, Color.BLUE, Color.ORANGE, Color.GREEN, Color.RED};
    private Circle counterUI[][];
    private boolean enabled;
    public DisplayedBoard(GameBoard gb){
        //Pass the Game Board;
        //this.gb = gb;
        this.setPrefSize(boardWidth,boardHeight);
        enabled = true;

        counterUI = new Circle[11][11];

        //Draw the board UI
        Rectangle background = new Rectangle(0,0,boardWidth,boardHeight);
        background.setFill(Color.WHITE);
        this.getChildren().add(background);

        boolean colorSwitch;
        Color curColor = Color.WHITE;

        double tileXSpan = boardWidth/11;
        double tileYSpan = boardHeight/11;

        //Inside Board
        for(int i = 0; i < 3; i++) {
            if(i == 0 || i == 2)
                colorSwitch = true;
            else
                colorSwitch = false;
            for(int j = 0; j < 3; j++){
                if(!colorSwitch)
                    curColor = tileColor[0];
                else
                    curColor = tileColor[1];
                colorSwitch = !colorSwitch;

                Rectangle rect = new Rectangle((1 + 3 * j) * tileXSpan, (1 + 3 * i) * tileYSpan, 3 * tileXSpan, 3 *tileYSpan);
                rect.setFill(curColor);
                getChildren().add(rect);

            }
        }

        //Edges
        Rectangle rectUL = new Rectangle(0,0,tileXSpan,tileYSpan);
        Rectangle rectUR = new Rectangle(10 * tileXSpan,0,tileXSpan,tileYSpan);
        Rectangle rectLL = new Rectangle(0,10 * tileYSpan,tileXSpan,tileYSpan);
        Rectangle rectLR = new Rectangle(10 * tileXSpan,10 * tileYSpan,tileXSpan,tileYSpan);

        getChildren().addAll(rectUL, rectUR, rectLL, rectLR);

        //Lines
        for( int i = 0;  i < 12; i++) {
            Line hline, vline;

            if(i == 4 || i == 7){
                hline = new Line(tileXSpan, i* tileYSpan, 10 * tileXSpan, i * tileYSpan);
                vline = new Line(i * tileXSpan, tileYSpan, i * tileXSpan, 10 * tileYSpan);
                hline.setStrokeWidth(3);
                vline.setStrokeWidth(3);
                getChildren().addAll(hline, vline);
            }

                hline = new Line(0, i * tileYSpan, boardWidth, i* tileYSpan);
                vline = new Line(i * tileXSpan, 0, i * tileXSpan, boardHeight);

                if(i < 2 || i > 9){
                    hline.setStrokeWidth(5);
                    vline.setStrokeWidth(5);
                }

                getChildren().addAll(hline, vline);
        }
        //Prepare All circles
        double cAdjustX = tileXSpan * 0.5;
        double cAdjustY = tileYSpan * 0.5;
        double radius = 15;

        for(int row = 0; row < 11; row++){
            for(int col = 0; col < 11; col++) {
                counterUI[row][col] = new Circle(col * tileXSpan + cAdjustX, row * tileYSpan + cAdjustY, radius, Color.RED);
                getChildren().add(counterUI[row][col]);
                counterUI[row][col].setVisible(false);
            }
        }

        //Listener Method for Mouse Click
        setOnMouseClicked(this);
    }

    public void updateDisplay() {
        //int[][] gameBoard = GameClient.getGameBoard();
        Color curColor = Color.WHITE;
        int[][]board = GameClient.getGameBoard();
        int[] last = GameClient.getLastCounter();
        for(int i = 0; i < 11; i++){
            for(int j = 0; j < 11; j++){
                System.out.print(board[i][j] + " ");

                if(board[i][j] == 0)counterUI[i][j].setVisible(false);
                else
                {
                    /*if(last != null && i == last[0] && j == last[1])
                        counterUI[i][j].setVisible(false);
                    else*/
                        counterUI[i][j].setVisible(true);
                    curColor = counterColor[board[i][j] - 1];

                }
                counterUI[i][j].setFill(curColor);

            }
            System.out.println();
        }

        //System.out.println("display updated!");

    }

    @Override
    public void handle(MouseEvent event) {
        if(!enabled) return;
        double sourceX = event.getX();
        double sourceY = event.getY();

        double xSpan = this.boardWidth / 11;
        double ySpan = this.boardHeight / 11;

        int pointCol = (int) (sourceX / xSpan);
        int pointRow = (int) (sourceY / ySpan);

        //animatePlace(pointRow, pointCol, Color.RED);
        Network.MoveMessage m = new Network.MoveMessage();
        m.row = pointRow;
        m.col = pointCol;
        m.counter = GameClient.getPlayerCounter();
        m.dir = Direction.S;
        GameClient.send(m);
    }

    public void animatePlace(int row, int col){
        System.out.println("ANIMATE " + row + ", "+ col);
        Circle counter = counterUI[row][col];
        Color color = (Color)counter.getFill();
        counter.setVisible(true);
        Color origin = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
        FillTransition ft = new FillTransition(Duration.seconds(0.7), counter ,origin, color);
        ft.play();
    }

    public void setControl(boolean enabled){
        this.enabled = enabled;
    }
}
