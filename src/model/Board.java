package model;

import java.util.ArrayList;
import java.util.List;
import config.Constants;

public class Board {
    private final int rows;
    private final int cols;
    private char[][] grid;
    private List<Piece> placedPieces;
    private String gameType;

    public Board(int rows, int cols, String gameType) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        this.placedPieces = new ArrayList<>();
        this.gameType = gameType;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }
    }

    public Board(Board other) {
        this.rows = other.rows;
        this.cols = other.cols;
        this.gameType = other.gameType;
        this.grid = new char[rows][cols];
        this.placedPieces = new ArrayList<>(other.placedPieces);
        
        // salin grid
        for (int i = 0; i < rows; i++) {
            System.arraycopy(other.grid[i], 0, this.grid[i], 0, cols);
        }
    }

    public boolean canPlacePiece(Piece piece, Position pos) {
        List<Position> piecePositions = piece.getOccupiedPositions();
        
        // cek untuk setiap posisi yang akan ditempati piece
        for (Position piecePos : piecePositions) {
            int newRow = pos.getRow() + piecePos.getRow();
            int newCol = pos.getCol() + piecePos.getCol();
            
            // cek apakah posisi valid dalam board
            if (!new Position(newRow, newCol).isValid(rows, cols)) {
                return false;
            }
            
            // cek apakah posisi sudah ditempati
            if (grid[newRow][newCol] != '.') {
                return false;
            }
        }
        
        return true;
    }

    public void placePiece(Piece piece, Position pos) {
        List<Position> piecePositions = piece.getOccupiedPositions();
        
        // menempatkan piece pada grid
        for (Position piecePos : piecePositions) {
            int newRow = pos.getRow() + piecePos.getRow();
            int newCol = pos.getCol() + piecePos.getCol();
            grid[newRow][newCol] = piece.getSymbol();
        }
        
        placedPieces.add(piece);
    }

    public void removePiece(Piece piece, Position pos) {
        List<Position> piecePositions = piece.getOccupiedPositions();
        
        // Hapus piece dari grid
        for (Position piecePos : piecePositions) {
            int newRow = pos.getRow() + piecePos.getRow();
            int newCol = pos.getCol() + piecePos.getCol();
            grid[newRow][newCol] = '.';
        }
        
        placedPieces.remove(piece);
    }

    public boolean isSolved() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    public void printBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char symbol = grid[i][j];
                if (symbol != '.') {
                    int colorIndex = symbol - 'A';
                    if (colorIndex >= 0 && colorIndex < Constants.COLORS.length) {
                        System.out.print(Constants.COLORS[colorIndex] + symbol + Constants.ANSI_RESET);
                    } else {
                        System.out.print(symbol);
                    }
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public char[][] getGrid() {
        return grid;
    }

    public List<Piece> getPlacedPieces() {
        return placedPieces;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(grid[i][j]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}