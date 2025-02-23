package utils;

import model.Piece;
import java.io.*;
import java.util.*;

public class FileHandler {
    public static class PuzzleInput {
        public int rows;
        public int cols;
        public int numPieces;
        public String gameType;
        public List<Piece> pieces;
        public int totalBlocks;

        public PuzzleInput(int rows, int cols, int numPieces, String gameType, List<Piece> pieces) {
            this.rows = rows;
            this.cols = cols;
            this.numPieces = numPieces;
            this.gameType = gameType;
            this.pieces = pieces;

            this.totalBlocks = pieces.stream()
                                   .mapToInt(Piece::getBlockCount)
                                   .sum();
                                   
            if (gameType.equals("DEFAULT") && 
                totalBlocks != rows * cols) {
                throw new IllegalArgumentException(
                    "Total blok (" + totalBlocks + 
                    ") tidak sesuai dengan ukuran papan (" + (rows * cols) + ")!"
                );
            }
        }
    }

    public static PuzzleInput readInput(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // baca dimensi board dan jumlah piece
            String dimensionLine = reader.readLine();
            if (dimensionLine == null) {
                throw new IllegalArgumentException("File kosong");
            }

            String[] dimensions = dimensionLine.trim().split("\\s+");
            if (dimensions.length != 3) {
                throw new IllegalArgumentException("Format input tidak valid!");
            }

            int rows = Integer.parseInt(dimensions[0]);
            int cols = Integer.parseInt(dimensions[1]);
            int numPieces = Integer.parseInt(dimensions[2]);

            if (rows <= 0 || cols <= 0 || numPieces <= 0 || numPieces > 26) {
                throw new IllegalArgumentException(
                    "Jumlah pieces atau dimensi tidak valid!"
                );
            }

            // baca tipe game
            String gameType = reader.readLine().trim();
            if (!gameType.equals("DEFAULT")) {
                throw new IllegalArgumentException("Game type tidak valid!");
            }

            // baca piece-piece
            List<Piece> pieces = new ArrayList<>();
            String line;
            List<String> currentPieceLines = new ArrayList<>();
            char currentSymbol = '\0';
            int piecesRead = 0;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // cari karakter pertama yang bukan spasi
                char firstNonSpace = '\0';
                for (char c : line.toCharArray()) {
                    if (c != ' ') {
                        firstNonSpace = c;
                        break;
                    }
                }

                if (firstNonSpace < 'A' || firstNonSpace > 'Z') {
                    throw new IllegalArgumentException(
                        "Piece symbol tidak valid: " + firstNonSpace
                    );
                }

                if (currentSymbol == '\0' || firstNonSpace != currentSymbol) {
                    // simpan piece sebelumnya jika ada
                    if (!currentPieceLines.isEmpty()) {
                        pieces.add(createPieceWithLeadingSpaces(currentSymbol, currentPieceLines));
                        piecesRead++;
                        currentPieceLines = new ArrayList<>();
                    }
                    currentSymbol = firstNonSpace;
                }
                currentPieceLines.add(line); // simpan line dengan spasi awalnya
            }

            // tambahkan piece terakhir
            if (!currentPieceLines.isEmpty()) {
                pieces.add(createPieceWithLeadingSpaces(currentSymbol, currentPieceLines));
                piecesRead++;
            }

            if (piecesRead != numPieces) {
                throw new IllegalArgumentException(
                    "Jumlah pieces tidak sesuai! (Seharusnya: " + numPieces + 
                    ", tetapi jumlah pieces saat ini: " + piecesRead + ")"
                );
            }

            return new PuzzleInput(rows, cols, numPieces, gameType, pieces);

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File tidak ditemukan!");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format input tidak valid!");
        }
    }

    private static Piece createPieceWithLeadingSpaces(char symbol, List<String> lines) {
        try {
            // normalisasi line 
            List<String> normalizedLines = new ArrayList<>();
            
            // minimum leading spaces
            int minLeadingSpaces = Integer.MAX_VALUE;
            for (String line : lines) {
                int leadingSpaces = 0;
                while (leadingSpaces < line.length() && line.charAt(leadingSpaces) == ' ') {
                    leadingSpaces++;
                }
                if (leadingSpaces < line.length()) { // hanya dihitung jika line tidak spasi semua
                    minLeadingSpaces = Math.min(minLeadingSpaces, leadingSpaces);
                }
            }

            // hapus minimum leading spaces
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String normalizedLine = line.substring(minLeadingSpaces);
                normalizedLines.add(normalizedLine);
            }

            return new Piece(symbol, normalizedLines);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Format piece tidak valid: " + e.getMessage());
        }
    }

    public static void writeSolution(String filename, String solution, long executionTime, long iterations) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Solusi:");
            writer.println(solution);
            writer.println();
            writer.printf("Waktu eksekusi: %d ms%n", executionTime);
            writer.printf("Jumlah iterasi: %d%n", iterations);
        } catch (IOException e) {
            System.err.println("Gagal menulis ke file: " + e.getMessage());
        }
    }
}