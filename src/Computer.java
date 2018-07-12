import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Computer {
    private GameBoard board;
    private GameBoard boardCopy;
    private int depthLimit;
    //Time measurement purposes
    private long startTime;
    private long endTime;
    private long duration;

    private ArrayList<int[]> placementRestrict;
    public Computer(GameBoard board) {
        this.board = board;
        depthLimit = board.getPlayerAmount() + 3;

        //Placement Restriction #2 for computers.
        placementRestrict = new ArrayList<int[]>();

        //Elements 0 and 1 are first, elements 2 and 3 are second.
        placementRestrict.add(new int[]{1, 0, 0, 9});
        placementRestrict.add(new int[]{1, 10, 0, 1});
        placementRestrict.add(new int[]{9, 0, 10, 9});
        placementRestrict.add(new int[]{9, 10, 10, 1});
        placementRestrict.add(new int[]{0, 1, 9, 0});
        placementRestrict.add(new int[]{10, 1, 1, 0});
        placementRestrict.add(new int[]{0, 9, 9, 10});
        placementRestrict.add(new int[]{10, 9, 1, 10});
    }
    public void move()
    {
        if(board.currentPhase() == 0)
        {
            ArrayList<int[]> emptyPos = board.getEmptyPositions();
            int randomPos = -1;
            int counter = board.getNextTurn();
            do{
                if(randomPos >= 0)emptyPos.remove(randomPos);
                randomPos = ThreadLocalRandom.current().nextInt(0, emptyPos.size());
            }
            while(!validCounterPos(emptyPos.get(randomPos)[0],emptyPos.get(randomPos)[1],counter)
                    && emptyPos.size() > 1);
            //System.out.println("Empty Position Size: "+size);

            board.placeCounter(emptyPos.get(randomPos)[0], emptyPos.get(randomPos)[1] ,counter);
            //Change Phases
            /*if(board.getCountersPlaced() == 45 && board.currentPhase() == 0)board.nextPhase();

            if(!board.isPlayerNextTurn())move();*/
        }
        else
        {
            int counter = board.getNextTurn();
            boardCopy = board.deepCopy();
            //boardCopy.printMovedList();
            PairScores chosenMove;

            if(counter == 2)startTime = System.nanoTime();
            chosenMove = maxN(0, counter, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
            if(counter == 2)
            {
                endTime = System.nanoTime();
                duration = endTime - startTime;
                System.out.println("Time taken: "+ duration);
            }
            //System.out.println("CHOSEN MOVE: "+chosenMove.getRow() + " " + chosenMove.getCol());
            board.moveCounter(chosenMove.getRow(), chosenMove.getCol(), counter);
        }
    }
    private ArrayList<Move> getCounterPositions(int counter, GameBoard board)//Get all positions of specific counter that can be moved
    {
        //Change this to return ArrayList of Move Class instead :v
        ArrayList counterPositions = new ArrayList<Move>();

        for(int i = 0; i< 10; i++)
        {
            //Check Top Squares
            if(board.getBoard()[0][i] == counter && board.getBoard()[1][i] == 0)
                counterPositions.add(new Move(0,i,counter,"Move", Direction.S));
            //Check Bottom Squares
            if(board.getBoard()[10][i] == counter && board.getBoard()[9][i] == 0)
                counterPositions.add(new Move(10,i,counter,"Move", Direction.S));
            //Check Left Squares
            if(board.getBoard()[i][0] == counter && board.getBoard()[i][1] == 0)
                counterPositions.add(new Move(i,0,counter,"Move", Direction.S));
            //Check Right Squares
            if(board.getBoard()[i][10] == counter && board.getBoard()[i][9] == 0)
                counterPositions.add(new Move(i,10,counter,"Move", Direction.S));
        }
        return counterPositions;
    }


    private boolean validCounterPos(int row, int col, int counter)
    {
        boolean valid1, valid2;
        valid1 = false;
        valid2 = false;
        //Check whether it is a good position to put a counter.

        //Check whether the position intercepts own counter on the other side.
        for(int i = 1; i < 10; i++)
        {
            if((col == 0 || col == 10) && boardCopy.getBoard()[row][i] != 0) {
                valid1 = true;
                break;
            }

            if((row == 0 || row == 10) && boardCopy.getBoard()[i][col] != 0) {
                valid1 = true;
                break;
            }
        }

        if(!valid1)
        {
            if(col == 0 && boardCopy.getBoard()[row][10] != counter)
                valid1 = true;
            else if(col == 10 && boardCopy.getBoard()[row][0] != counter)
                valid1 = true;
            else
                valid1 = false;

            if(row == 0 && boardCopy.getBoard()[10][col] != counter)
                valid1 = true;
            else if(row == 10 && boardCopy.getBoard()[col][0] != counter)
                valid1 = true;
            else
                valid1 = false;

        }
        //if(valid1)System.out.println("COUNTER DOES NOT INTERCEPT OWN COUNTER.");
        //else System.out.println("COUNTER DOES INTERCEPT OWN COUNTER.");

        //If the counter is on an edge, check whether it intercepts the other counter when moved.
        valid2 = true;
        for(int[] checker : placementRestrict)
        {
            if(row == checker[0] && col == checker[1])
            {
                if(board.getBoard()[checker[2]][checker[3]] != counter)
                    valid2 = true;
                else
                    valid2 = false;
                break;
            }

            else if(row == checker[2] && col == checker[3])
            {
                if(board.getBoard()[checker[0]][checker[1]] != counter)
                    valid2 = true;
                else
                    valid2 = false;
                break;
            }
        }
        //if(valid2)System.out.println("COUNTER DOES NOT INTERCEPT EDGE COUNTERS, OR IMPOSSIBLE TO DO SO.");
        //else System.out.println("COUNTER DOES INTERCEPT EDGE COUNTERS");
        return valid1 && valid2;
    }

    private PairScores maxN(int depth, int counter, int score1, int score2, int score3, int score4)
    {

        //Using array of scores is impossible, since arrays will be passed by reference.
        //We want them to NOT be passed by reference, as per regular minimax algorithm.

        int bestRow = -1;
        int bestCol = -1;
        //Stop Traversing the tree here.
        //Get the analysis of the current node.
        if(boardCopy.getCountersMoved() == 36 ||
                depth == depthLimit||
                !boardCopy.isAnyMovementPossible())
        {
            int[] curscores;
            curscores = boardCopy.getFullAnalysis();
            return new PairScores(-1,-1,curscores);
        }

        //Get all possible moves for the current counter.
        ArrayList<Move> choices = getCounterPositions(counter, boardCopy);
        PairScores ps;
        for(Move c : choices)
        {
            boardCopy.moveCounter(c.getRow(), c.getCol(), counter);
            if(boardCopy.isAnyMovementPossible())boardCopy.skipTurn(boardCopy.getNextTurn());

            ps = maxN(depth + 1, boardCopy.getNextTurn(),score1, score2, score3, score4);
            boardCopy.undoMove();

            int []x = new int[boardCopy.getPlayerAmount()];

            for(int i = 0; i< boardCopy.getPlayerAmount();i++)
                x[i] = ps.getScores()[i];

            //The following lines will matter only at the very root of the recursion.
            if(     (counter == 1 && x[counter-1] > score1)
                    ||  (counter == 2 && x[counter-1] > score2)
                    ||  (counter == 3 && x[counter-1] > score3)
                    ||  (counter == 4 && x[counter-1] > score4))
            {
                score1 = x[0];
                score2 = x[1];
                if(boardCopy.getPlayerAmount() >= 3)score3 = x[2];
                if(boardCopy.getPlayerAmount() == 4)score4 = x[3];

                //Get the col and row FROM c, NOT vc.
                //This is important.
                bestRow = c.getRow();
                bestCol = c.getCol();
            }
        }
        return new PairScores(bestRow, bestCol, new int[]{score1, score2, score3, score4});

    }
}
