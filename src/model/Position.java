package model;

public class Position {
    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && col == position.col;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }

    // method untuk mendapatkan posisi baru hasil pergeseran
    public Position offset(int rowOffset, int colOffset) {
        return new Position(this.row + rowOffset, this.col + colOffset);
    }

    // method untuk memeriksa apakah posisi valid dalam board berukuran rows x cols
    public boolean isValid(int rows, int cols) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
}