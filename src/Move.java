public class Move {
    private int row;
    private int col;
    private String type;
    private int counter;
    private Direction dir;

    public Move(int r, int c, int count, String t, Direction d)
    {
        row = r;
        col = c;
        counter = count;
        type = t;// t is either PLACE or MOVE.
        dir = d; // if t is PLACE, then d will always be S (for Static).
    }

    public int getRow()
    {
        return row;
    }
    public int getCol()
    {
        return col;
    }
    public int getCounter()
    {
        return counter;
    }
    public String getType()
    {
        return type;
    }
    public Direction getDir()
    {
        return dir;
    }

    public boolean equals(Move compareTo)
    {
        if(row != compareTo.getRow())return false;
        if(col != compareTo.getCol())return false;
        if(counter != compareTo.getCounter())return false;
        if(type != compareTo.getType())return false;
        if(dir != compareTo.getDir())return false;
        return true;
    }
}
