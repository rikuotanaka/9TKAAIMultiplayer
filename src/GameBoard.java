import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author erikk
 */
public class GameBoard {
    private int [][] board;
    private int numOfPlayers;
    private int numOfPlacedCounters;
    private int numOfMovedCounters;
    private int lastMovement;
    private int phase;
    private boolean[] isPlayer;
    private List<Move> placedList;
    private List<Move> movedList;
    private ArrayList<MovementData> movementData;
    //ROMBAK CLASS INI!!!
    public GameBoard(int nOP, boolean isPlayer[])
    {

        board = new int[11][11];
        this.isPlayer = isPlayer;
        numOfPlayers = nOP;
        numOfPlacedCounters = 0;
        numOfMovedCounters = 0;

        phase = 0;

        placedList = new ArrayList<Move>();
        movedList = new ArrayList<Move>();
        movementData = new ArrayList<MovementData>();
        //movementData.add(new MovementData("(9, 10) -> (9,9)", "B", "O", "G"));
        randomizeNeutralCounters();
        lastMovement = nOP;
    }


    public int getLastMovement()
    {
        return lastMovement;
    }

    public int getNextTurn()
    {
        int nextTurn = lastMovement;
        do {
            nextTurn ++;
            if (nextTurn > numOfPlayers) nextTurn = 1;
        }while(phase == 1 && !isMovementPossible(nextTurn));
        return nextTurn;
    }

    public void skip1Turn(){
        int temp = getNextTurn();
        do{
            lastMovement ++;
            if(lastMovement > numOfPlayers) lastMovement = 1;
        }while(phase == 1 && !isMovementPossible(lastMovement));
        if(phase == 0)
            updateRecentPHistory(temp, -1, -1);
        else
            updateRecentMHistory(temp, -1,-1, -1, -1);

    }

    public boolean isPlayerNextTurn()
    {
        int index = getNextTurn() - 1;
        return isPlayer[index];
    }

    public void resetBoard(){
        //Resets everything
        for(int i = 0; i < 11; i++){
            for(int j = 0; j < 11; j++){
                board[i][j] = 0;
            }
        }

        numOfPlacedCounters = 0;
        numOfMovedCounters = 0;

        phase = 0;

        placedList.clear();
        movedList.clear();

        randomizeNeutralCounters();
        lastMovement = numOfPlayers;
    }

    public void setLastMovement(int lastMove)
    {
        lastMovement = lastMove;
    }

    public int getPlayerAmount()
    {
        return numOfPlayers;
    }

    public int currentPhase()
    {
        return phase;
    }

    public void nextPhase()
    {
        phase++;
    }

    public void prevPhase()
    {
        phase--;
    }


    public int[][] getBoard()
    {
        return board;
    }

    //for testing
    public void setBoard(int[][] b)
    {
        this.board = b;

    }

    public ArrayList<MovementData> getMovementData() {
        return movementData;
    }

    //for rough visual
    public void printBoard()
    {
        for(int i = 0; i < 11; i++)
        {
            for(int j = 0; j < 11; j++)
            {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printMovedList()
    {
        System.out.println();
        for(Move m : movedList)
        {
            System.out.print("("+m.getRow() + ", "+m.getCol()+") ");
        }
        System.out.println();
        System.out.println();
    }

    public int getCountersPlaced()
    {
        return numOfPlacedCounters;
    }

    public int getCountersMoved()
    {
        return numOfMovedCounters;
    }
    public GameBoard deepCopy()
    {
        boolean isPlayer[] = new boolean[numOfPlayers];
        for(int i = 0; i < numOfPlayers; i++)
        {
            isPlayer[i] = this.isPlayer[i];
        }
        GameBoard gb = new GameBoard(numOfPlayers,isPlayer);
        copyBoard(this.board, gb.board);
        gb.numOfPlayers = this.numOfPlayers;
        gb.phase = this.phase;

        gb.lastMovement = this.lastMovement;
        gb.numOfPlacedCounters = this.numOfPlacedCounters;
        gb.numOfMovedCounters = this.numOfMovedCounters;

        copyList(this.placedList, gb.placedList);
        copyList(this.movedList, gb.movedList);

        return gb;
    }

    private void copyBoard(int[][] source, int [][] destination)
    {
        for(int i = 0; i< 11; i++)
        {
            for(int j = 0; j < 11; j++)
            {
                destination[i][j] = source [i][j];
            }
        }
    }

    public void copyList(List<Move> source, List<Move> destination)
    {
        for(Move m: source)
        {
            destination.add(new Move(m.getRow(), m.getCol(), m.getCounter(), m.getType(),m.getDir()));
        }
    }

    public void undoMove()
    {
        if(numOfPlacedCounters > 0)
        {
            Move m;
            if(phase == 0 || numOfMovedCounters == 0)
            {
                numOfPlacedCounters--;
                phase = 0;
                m = placedList.get(placedList.size() - 1);
                board[m.getRow()][m.getCol()] = 0;
                placedList.remove(placedList.size()-1);

            }
            else if(phase == 1)
            {
                numOfMovedCounters--;
                m = movedList.get(movedList.size()-1);
                board[m.getRow()][m.getCol()] = 0;
                if(m.getDir() == Direction.D)board[0][m.getCol()] = m.getCounter();
                else if(m.getDir() == Direction.U)board[10][m.getCol()] = m.getCounter();
                else if(m.getDir() == Direction.L)board[m.getRow()][10] = m.getCounter();
                else if(m.getDir() == Direction.R)board[m.getRow()][0] = m.getCounter();
                movedList.remove(movedList.size()-1);
            }
            lastMovement--;
            if(lastMovement == 0)lastMovement = numOfPlayers;
        }
    }
    public void randomizeNeutralCounters()
    {
        int pos1, pos2;
        int initPos1, initPos2;
        for(int i = 1; i <= 3; i++)
        {
            for(int j = 1; j <= 3; j++)
            {
                do{
                    pos1 = ThreadLocalRandom.current().nextInt(0, 3);
                    pos2 = ThreadLocalRandom.current().nextInt(0, 3);
                    initPos1 = (i-1) * 3 + 1;
                    initPos2 = (j-1) * 3 + 1;
                }while(pos1 + initPos1 == 1 || pos2 + initPos2 == 1 ||pos1 + initPos1 == 9 || pos2 + initPos2 == 9);
                placeCounter(initPos1 + pos1,initPos2 + pos2,5);
            }
        }
    }



    public ArrayList getEmptyPositions()
    {
        ArrayList emptyPositions = new ArrayList<int[]>();
        int temp[];
        for(int i = 1; i < 10; i++)
        {
            if(board[0][i] == 0)
            {
                temp = new int[2];
                temp[0] = 0;
                temp[1] = i;

                emptyPositions.add(temp);
            }

            if(board[10][i] == 0)
            {
                temp = new int[2];
                temp[0] = 10;
                temp[1] = i;

                emptyPositions.add(temp);

            }

            if(board[i][0] == 0)
            {
                temp = new int[2];
                temp[0] = i;
                temp[1] = 0;

                emptyPositions.add(temp);

            }

            if(board[i][10] == 0)
            {
                temp = new int[2];
                temp[0] = i;
                temp[1] = 10;

                emptyPositions.add(temp);

            }

        }
        return emptyPositions;
    }

    public Move getLastTurnCounter()
    {
        if(phase == 0 && placedList.size() == 0)return null;
        else if(phase == 1 && movedList.size() == 0)return placedList.get(placedList.size()-1);
        else if(phase == 0)
            return placedList.get(placedList.size()-1);
        else
            return movedList.get(movedList.size()-1);
    }


    public boolean placeCounter(int row, int col, int counter)
    {

        if(row < 0 || row > 10 || col < 0 || col > 10) return false;
        else if(row == 0 && col == 0)return false;
        else if(row == 0 && col == 10)return false;
        else if(row == 10 && col == 0)return false;
        else if(row == 10 && col == 10)return false;
        else if(board[row][col] != 0)return false;
        else if((row != 0 && row!=10) && (col !=0 && col != 10) && counter != 5 )return false;
        else
        {
            board[row][col] = counter;
            lastMovement = counter;
            numOfPlacedCounters++;
            if(counter!=5)
            {
                Move curMove = new Move(row, col, counter, "PLACE", Direction.S);
                placedList.add(curMove);
                updateRecentPHistory(counter, row, col);
            }
            if(numOfPlacedCounters == 45)phase = 1;
            return true;

        }

    }

    public boolean moveCounter(int row, int col, int counter)
    {
        if(row < 0 || row > 10 || col < 0 || col > 10) return false;
        else if(board[row][col] == 0 || board[row][col] == 5)return false;
        else
        {
            int counterTemp = board[row][col];
            //Prevent moving out of order
            if(counter != counterTemp)return false;
            //System.out.println("MOVING COUNTER: " + row + " " + col);

            int[] target = moveUntil(row, col);
            System.out.println("MOVE UNTIL : "+target[0]+", "+target[1]);
            if(target[0] == row && target[1] == col)
                return false;

            board[row][col] = 0;
            board[target[0]][target[1]]= counter;

            updateRecentMHistory(counter, row, col, target[0], target[1]);

            numOfMovedCounters ++;
            lastMovement = counter;
        }
        return true;
    }

    public void updateRecentPHistory(int counter, int row, int col){
        String content;
        if(row > -1 && col > -1)content = "(" + row + ", "+ col +")";
        else content = "-";
        if(counter == 1){
            movementData.add(new MovementData(content, "-", "-", "-"));
        }
        else{
            MovementData a = movementData.get(movementData.size() - 1);
            if(counter == 2)
                a.setBlueMove(content);
            else if (counter == 3)
                a.setOrangeMove(content);
            else if (counter == 4)
                a.setGreenMove(content);
        }
    }

    public void updateRecentMHistory(int counter, int row, int col, int row2, int col2){
        String content;
        if(row > -1 && col > -1)content = "(" + row + ", "+ col +") -> "+"("+row2+", "+col2+")";
        else content = "-";
        if(counter == 1){
            movementData.add(new MovementData(content, "-", "-", "-"));
        }
        else{
            MovementData a = movementData.get(movementData.size() - 1);
            if(counter == 2)
                a.setBlueMove(content);
            else if (counter == 3)
                a.setOrangeMove(content);
            else if (counter == 4)
                a.setGreenMove(content);
        }
    }

    public int[] moveUntil(int row, int col){
        int inc = 0;
        int ind;
        if(col == 0 || col == 10){// Move to the Left/ Right
            if(col == 0)inc = 1;
            if(col == 10)inc = -1;
            ind = col + inc;
            if(board[row][ind] != 0)
                return new int[]{row, col};
            while(board[row][ind + inc] == 0 && ind > 0 && ind < 10)
                ind += inc;
            return new int[]{row, ind};
        }
        else if(row == 0 || row == 10){ //Move to Top/Bottom
            if(row == 0)inc = 1;
            if(row == 10)inc = -1;
            ind = row + inc;
            if(board[ind][col] != 0)
                return new int[]{row, col};
            while(board[ind + inc][col] == 0 && ind > 0 && ind < 10)
                ind += inc;
            return new int[]{ind, col};
        }
        return new int[]{row, col};
    }

    public boolean isMovementPossible(int counter)
    {
        if(numOfMovedCounters == 36)return false;
        if(phase == 0)return true;
        int row, col;
        int c;
        int i=0;
        for(Move place : placedList)
        {
            i++;
            row = place.getRow();
            col = place.getCol();
            c = place.getCounter();
            if(c != counter) continue;
            //System.out.println("LAST CHECKING FOR: "+ row +" " +col);
            if(board[row][col] == 0) continue;
            if(row == 0)//Top
            {
                if(board[1][col] == 0)return true;
            }
            else if(row == 10)//Bottom
            {
                if(board[9][col] == 0)return true;
            }
            else if(col == 0)//Left
            {
                if(board[row][1] == 0)return true;
            }
            else if(col == 10)//Right
            {
                if(board[row][9] == 0)return true;
            }

        }

        return false;
    }

    public boolean isAnyMovementPossible()
    {
        for(int i=1; i< numOfPlayers + 1; i++)
        {
            if(isMovementPossible(i))return true;
        }

        return false;
    }

    public void skipTurn(int counter)
    {
        if(phase == 1 && !isMovementPossible(counter) && numOfMovedCounters < 36)
        {
            while(!isMovementPossible(counter))
            {
                counter++;
                if(counter > numOfPlayers)counter = 1;
            }

            //Set The Next Turn
            lastMovement = counter - 1;
        }
    }
    public int[] getPoints(){
        int[] points = new int[numOfPlayers];
        int temp;
        System.out.println(numOfPlayers);

        for(int i = 0; i < numOfPlayers; i++){
            points[i] = 0;
        }
        System.out.println(points[0]);

        for(int i = 1; i <= 3; i++) {
            for(int j = 1; j <= 3; j++) {
                temp = getRuler(i,j);
                if(temp > 0)points[temp-1]++;
            }
        }
        return points;
    }

    public int getRuler(int srow, int scol)//Get the Ruler of Specific Area.
    {

        int initRow = (srow - 1) * 3 + 1;
        int initCol = (scol - 1) * 3 + 1;
        int totalScore[];
        totalScore = new int[numOfPlayers];
        for(int i=0; i<numOfPlayers; i++)
            totalScore[i] = 0;

        int currentCounter;
        for(int i=0; i<3; i++)
        {
            for(int j=0; j<3; j++)
            {
                currentCounter = board[initRow + i][initCol + j];
                if(currentCounter > 0 && currentCounter != 5)totalScore[currentCounter - 1]++;
            }
        }
        int maxIndex = -1;
        int maxScore = 0;
        for(int i = 0; i< numOfPlayers; i++)
        {
            if(totalScore[i] > maxScore)
            {
                maxScore = totalScore[i];
                maxIndex = i;
            }
        }

        //Check wether it is a draw
        for(int i = 0; i < numOfPlayers; i++)
        {
            if(i == maxIndex) continue;
            if(maxScore == totalScore[i])return 0;
        }
        return maxIndex + 1;
    }

    public int getWinner()// Get The Winner of the game.
    {
        int totalScore[] = new int[numOfPlayers];
        for(int i = 0; i < numOfPlayers; i++ )
            totalScore[i] = 0;

        int currentRuler;

        for(int i = 1; i <= 3; i++ )
        {
            for(int j = 1; j <= 3; j++)
            {
                currentRuler = getRuler(i , j);
                if(currentRuler > 0)totalScore[currentRuler - 1]++;
            }
        }
        int maxIndex = -1;
        int maxScore = 0;

        for(int i = 0; i < numOfPlayers; i++)
        {
            if(totalScore[i] >= maxScore)//"The last of the tied players wins.
            {
                maxScore = totalScore[i];
                maxIndex = i;
            }
        }
        return maxIndex + 1;

    }

    public int getAnalysis(int counter)//Get Analysis on the current turn about specific counter.
    {
        int rulerScore = 100;
        int[] counterEachArea ={30, 15, 0, -15, -30};
        int closeOpponentsCounters = 90;
        int closeOwnCounters = -90;

        int score;
        int totalRuled = 0;
        int countersPerArea;

        //Get the total of currently ruled areas.
        for(int i = 1; i <= 3 ; i++)
        {
            for(int j = 1; j  <= 3; j++)
            {
                if(getRuler(i,j) == counter)totalRuled++;
            }
        }
        score = totalRuled * rulerScore;

        //Check each area, how many counters are there.
        //The more counters per area,  the less score is gained
        for(int i = 1; i <= 3; i++)
        {
            for(int j = 1; j <=3; j++)
            {
                countersPerArea = getCounterAmountArea(i,j,counter);
                if(countersPerArea > 4)countersPerArea = 4;
                score += counterEachArea[countersPerArea];
            }
        }

        //Check if the counters moved closes other player's counters, or closes own counters.
        for(Move m : movedList)
        {
            if(m.getCounter() == counter)
            {
                if(m.getRow() == 1)
                {
                    if(board[0][m.getCol()] != 0)
                    {
                        if(board[0][m.getCol()] != counter)score += closeOpponentsCounters;
                        else score += closeOwnCounters;
                    }
                }

                if(m.getRow() == 9)
                {
                    if(board[10][m.getCol()] != 0)
                    {
                        if(board[10][m.getCol()] != counter)score += closeOpponentsCounters;
                        else score += closeOwnCounters;
                    }
                }

                if(m.getCol() == 1)
                {
                    if(board[m.getRow()][0] != 0)
                    {
                        if(board[m.getRow()][0] != counter)score += closeOpponentsCounters;
                        else score += closeOwnCounters;
                    }
                }

                if(m.getCol() == 9)
                {
                    if(board[m.getRow()][10] != 0)
                    {
                        if(board[m.getRow()][10] != counter)score += closeOpponentsCounters;
                        else score += closeOwnCounters;
                    }
                }

            }
        }

        return score;
    }

    public int[] getFullAnalysis()//Get Analysis of all the players
    {
        int scores[] = new int[numOfPlayers];

        for(int i = 1; i <= numOfPlayers; i++)
        {
            scores[i-1] = getAnalysis(i);
        }
        return scores;
    }

    public int getCounterAmountArea(int srow, int scol, int counter)
    {
        int count = 0;
        int initRow = (srow - 1) * 3 + 1;
        int initCol = (scol - 1) * 3 + 1;

        for(int i = 0; i < 3 ; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                if(board[initRow+i][initCol+j] == counter)
                    count++;
            }
        }

        return count;
    }

}