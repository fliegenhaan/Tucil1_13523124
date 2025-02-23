package solver;

import model.Board;
import model.Piece;
import model.Position;
import java.util.*;
import java.util.function.Consumer;

public class PuzzleSolver {
    private Board board;
    private List<Piece> pieces;
    private long iterations;
    private long startTime;
    private long executionTime;
    private Board solution;
    private Consumer<Board> progressCallback;

    public PuzzleSolver(Board board, List<Piece> pieces) {
        this.board = board;
        this.pieces = pieces;
        this.iterations = 0;
        this.solution = null;
    }

    public void setProgressCallback(Consumer<Board> callback) {
        this.progressCallback = callback;
    }

    private void updateProgress() {
        if (progressCallback != null) {
            progressCallback.accept(new Board(board));
        }
    }

    public boolean solve() {
        startTime = System.currentTimeMillis();
        boolean result = solveRecursive(0);
        executionTime = System.currentTimeMillis() - startTime;
        return result;
    }

    // fungsi rekursif utk mencoba semua kemungkinan penempatan piece
    // mencoba setiap piece secara berurutan dari indeks 0 sampai n-1
    private boolean solveRecursive(int pieceIndex) {
        // basis: semua piece sudah dicoba
        if (pieceIndex >= pieces.size()) {
            if (board.isSolved()) {
                solution = new Board(board);
                return true;
            }
            return false;
        }

        // mendapatkan piece yg akan digunakan utk iterasi ini dan mendapatkan semua orientasi yang mungkin
        Piece currentPiece = pieces.get(pieceIndex);
        List<Piece> orientations = currentPiece.getAllOrientations();
        
        // coba pada setiap orientasi piece
        for (Piece orientation : orientations) {
            // coba setiap posisi pada board
            for (int row = 0; row < board.getRows(); row++) {
                for (int col = 0; col < board.getCols(); col++) {
                    Position pos = new Position(row, col);
                    
                    // jika berhasil menempatkan piece
                    if (board.canPlacePiece(orientation, pos)) {
                        iterations++;         // menginkremen jumlah iterasi
                        board.placePiece(orientation, pos);
                        updateProgress(); 
                        
                        // rekursi (coba piece selanjutnya)
                        if (solveRecursive(pieceIndex + 1)) {
                            return true;
                        }
                        
                        // backtrack: hapus piece jika tidak mendapat solusi
                        board.removePiece(orientation, pos);
                        updateProgress();
                    }
                }
            }
        }
        
        return false;
    }

    public long getIterations() {
        return iterations;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public Board getSolution() {
        return solution;
    }
}