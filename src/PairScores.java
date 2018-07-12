public class PairScores {
    private int row;
    private int col;
    private int [] scores;

    public PairScores(int row, int col, int[] scores)
    {
        this.row = row;
        this.col = col;
        this.scores = scores;
    }

    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }

    public int[] getScores()
    {
        return scores;
    }
}
