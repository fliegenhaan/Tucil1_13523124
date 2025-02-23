package main;

import model.Board;
import solver.PuzzleSolver;
import utils.FileHandler;
import utils.FileHandler.PuzzleInput;

import javax.swing.*;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.print("Pilih mode (1: GUI, 2: CLI): ");
        Scanner scanner = new Scanner(System.in);
        int mode = scanner.nextInt();
        
        if (mode == 1) {
            SwingUtilities.invokeLater(() -> {
                PuzzleGUI gui = new PuzzleGUI();
                gui.setVisible(true);
            });
        } else {
            runCLI(scanner);
        }
    }

    private static void runCLI(Scanner scanner) {
        try {
            // Baca input file
            System.out.print("Masukkan nama file input (.txt): ");
            scanner.nextLine(); 
            String inputFilename = scanner.nextLine();
            PuzzleInput input = FileHandler.readInput(inputFilename);
            
            // Buat board dan solver
            Board board = new Board(input.rows, input.cols, input.gameType);
            PuzzleSolver solver = new PuzzleSolver(board, input.pieces);
            
            boolean hasSolution = solver.solve();
            
            if (hasSolution) {
                System.out.println("\nSolusi ditemukan:");
                solver.getSolution().printBoard();
                System.out.printf("\nWaktu eksekusi: %d ms%n", solver.getExecutionTime());
                System.out.printf("Jumlah iterasi: %d%n", solver.getIterations());
                
                System.out.print("\nApakah anda ingin menyimpan solusi? (ya/tidak): ");
                String saveResponse = scanner.nextLine().toLowerCase();
                
                if (saveResponse.equals("ya")) {
                    System.out.print("\nMasukkan nama file output (.txt): ");
                    String outputFilename = scanner.nextLine();
                    FileHandler.writeSolution(
                        outputFilename,
                        solver.getSolution().toString(),
                        solver.getExecutionTime(),
                        solver.getIterations()
                    );
                    System.out.println("Solusi berhasil disimpan ke " + outputFilename);
                }
            } else {
                System.out.println("Tidak ada solusi yang ditemukan.");
            }
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}