package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Piece {
    private char symbol;
    private boolean[][] shape;
    private int height;
    private int width;
    private int blockCount;

    public Piece(char symbol, List<String> pieceLines) {
        this.symbol = symbol;
        this.blockCount = 0;
        
        // menentukan dimensi piece
        this.height = pieceLines.size();
        this.width = pieceLines.stream()
                    .mapToInt(String::length)
                    .max()
                    .orElse(0);

        // inisialisasi shape
        this.shape = new boolean[height][width];
        for (int i = 0; i < height; i++) {
            String line = pieceLines.get(i);
            for (int j = 0; j < line.length(); j++) {
                shape[i][j] = (line.charAt(j) == symbol);
                if (shape[i][j]) {
                    blockCount++;
                }
            }
        }
        
        if (!isConnected()) {
            throw new IllegalArgumentException("Piece harus terhubung!");
        }
    }

    // salin constructor
    public Piece(Piece other) {
        this.symbol = other.symbol;
        this.height = other.height;
        this.width = other.width;
        this.shape = new boolean[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(other.shape[i], 0, this.shape[i], 0, width);
        }
    }

    public char getSymbol() {
        return symbol;
    }

    public boolean[][] getShape() {
        return shape;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getBlockCount() {
        return blockCount;
    }

    // rotasi piece 90 derajat searah jarum jam
    public void rotate() {
        boolean[][] newShape = new boolean[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newShape[j][height - 1 - i] = shape[i][j];
            }
        }
        shape = newShape;
        int tempDim = height;
        height = width;
        width = tempDim;
    }

    // pencerminan piece secara horizontal
    public void flip() {
        boolean[][] newShape = new boolean[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newShape[i][width - 1 - j] = shape[i][j];
            }
        }
        shape = newShape;
    }

    // generate semua kemungkinan orientasi piece
    public List<Piece> getAllOrientations() {
        List<Piece> orientations = new ArrayList<>();
        Piece current = new Piece(this);
        
        // 4 rotasi tanpa flip
        for (int i = 0; i < 4; i++) {
            orientations.add(new Piece(current));
            current.rotate();
        }
        
        // flip piece dan 4 rotasi lagi
        current = new Piece(this);
        current.flip();
        for (int i = 0; i < 4; i++) {
            orientations.add(new Piece(current));
            current.rotate();
        }

        return orientations;
    }

    // mendapatkan list posisi yang ditempati piece relatif terhadap (0,0)
    public List<Position> getOccupiedPositions() {
        List<Position> positions = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (shape[i][j]) {
                    positions.add(new Position(i, j));
                }
            }
        }
        return positions;
    }

    // method untuk mengecek apakah piece terhubung
    private boolean isConnected() {
        if (blockCount == 0) return true;
        
        boolean[][] visited = new boolean[height][width];
        int connectedBlocks = 0;
        
        // cari posisi blok pertama
        int startI = -1, startJ = -1;
        outer:
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (shape[i][j]) {
                    startI = i;
                    startJ = j;
                    break outer;
                }
            }
        }
        
        // hitung blok yang terhubung
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startI, startJ});
        
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}}; // atas, bawah, kiri, kanan
        
        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int i = current[0], j = current[1];
            
            if (i < 0 || i >= height || j < 0 || j >= width || 
                !shape[i][j] || visited[i][j]) continue;
            
            visited[i][j] = true;
            connectedBlocks++;
            
            for (int[] dir : directions) {
                stack.push(new int[]{i + dir[0], j + dir[1]});
            }
        }
        
        return connectedBlocks == blockCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                sb.append(shape[i][j] ? symbol : '.');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}