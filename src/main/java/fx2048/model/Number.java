package fx2048.model;

public class Number {

    private int col;
    private int row;
    private int value;

    private Runnable onMove = () -> {
    };

    public Number(int col, int row, int value) {
        this.col = col;
        this.row = row;
        this.value = value;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
        onMove.run();
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
        onMove.run();
    }

    public int getValue() {
        return value;
    }

    public void setOnMove(Runnable onMove) {
        this.onMove = onMove;
    }

}
