package main;

import model.Board;
import solver.PuzzleSolver;
import utils.FileHandler;
import utils.FileHandler.PuzzleInput;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PuzzleGUI extends JFrame {
    private JButton chooseFileBtn;
    private JButton solveBtn;
    private JButton saveBtn;
    private JButton saveImageBtn;
    private JLabel statusLabel;
    private JPanel boardPanel;
    private JLabel timeLabel;
    private JLabel iterationLabel;
    private PuzzleInput input;
    private Board solution;
    private Board currentBoard;
    private long executionTime;
    private long iterations;
    private Color[] pieceColors;
    private JSlider speedSlider; 
    private int animationDelay = 100; 

    public PuzzleGUI() {
        initializeColors();
        setupWindow();
        setupComponents();
        setupLayout();
    }

    private void initializeColors() {
        pieceColors = new Color[26];
        pieceColors[0] = new Color(255, 87, 87);   
        pieceColors[1] = new Color(98, 182, 87);   
        pieceColors[2] = new Color(255, 189, 74);  
        pieceColors[3] = new Color(74, 137, 255);  
        pieceColors[4] = new Color(187, 74, 255);  
        pieceColors[5] = new Color(74, 255, 231);  
        for (int i = 6; i < 26; i++) {
            pieceColors[i] = new Color(
                (int)(Math.random() * 200) + 55,
                (int)(Math.random() * 200) + 55,
                (int)(Math.random() * 200) + 55
            );
        }
    }

    private void setupWindow() {
        setTitle("IQ Puzzler Pro Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
    }

    private void setupComponents() {
        chooseFileBtn = new JButton("Pilih file input");
        solveBtn = new JButton("Selesaikan Puzzle");
        saveBtn = new JButton("Simpan Solusi");
        saveImageBtn = new JButton("Simpan sebagai gambar");
        statusLabel = new JLabel("Pilih sebuah file input");
        timeLabel = new JLabel("Time: -");
        iterationLabel = new JLabel("Iterations: -");
        
        speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 100);
        speedSlider.setMajorTickSpacing(200);
        speedSlider.setMinorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(_ -> {
            animationDelay = 1000 - speedSlider.getValue();
        });

        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(400, 400));
        boardPanel.setBackground(Color.WHITE);

        chooseFileBtn.addActionListener(_ -> chooseFile());
        solveBtn.addActionListener(_ -> solvePuzzle());
        saveBtn.addActionListener(_ -> saveSolution());

        solveBtn.setEnabled(false);
        saveBtn.setEnabled(false);

        saveImageBtn.setEnabled(false);
        saveImageBtn.addActionListener(_ -> saveAsImage());
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(chooseFileBtn);
        buttonPanel.add(solveBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(saveImageBtn); 

        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.setBorder(BorderFactory.createTitledBorder("Kecepatan animasi"));
        sliderPanel.add(speedSlider);

        controlPanel.add(buttonPanel);
        controlPanel.add(sliderPanel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(statusLabel);
        infoPanel.add(timeLabel);
        infoPanel.add(iterationLabel);

        add(controlPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                input = FileHandler.readInput(selectedFile.getPath());
                statusLabel.setText("File berhasil dimuat");
                solveBtn.setEnabled(true);
                solution = null;
                currentBoard = null;
                saveBtn.setEnabled(false);
                timeLabel.setText("Waktu Eksekusi: -");
                iterationLabel.setText("Jumlah Iterasi: -");
                boardPanel.repaint();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Gagal membaca file: " + e.getMessage(),
                    "Gagal",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void solvePuzzle() {
        if (input == null) return;

        solveBtn.setEnabled(false);
        chooseFileBtn.setEnabled(false);
        statusLabel.setText("Menyelesaikan puzzle...");
        
        new SwingWorker<Void, Board>() {
            @Override
            protected Void doInBackground() throws Exception {
                Board board = new Board(input.rows, input.cols, input.gameType);
                PuzzleSolver solver = new PuzzleSolver(board, input.pieces);
                
                solver.setProgressCallback(currentState -> {
                    try {
                        Thread.sleep(animationDelay);
                        publish(currentState);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                
                boolean hasSolution = solver.solve();
                
                if (hasSolution) {
                    solution = solver.getSolution();
                    executionTime = solver.getExecutionTime();
                    iterations = solver.getIterations();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Solusi didapatkan!");
                        timeLabel.setText(String.format("Waktu Eksekusi: %d ms", executionTime));
                        iterationLabel.setText(String.format("Jumlah Iterasi: %d", iterations));
                        saveBtn.setEnabled(true);
                        saveImageBtn.setEnabled(true); 
                        boardPanel.repaint();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Tidak ada solusi!");
                    });
                }
                
                SwingUtilities.invokeLater(() -> {
                    solveBtn.setEnabled(true);
                    chooseFileBtn.setEnabled(true);
                });
                
                return null;
            }

            @Override
            protected void process(List<Board> chunks) {
                // Update board display dengan state terbaru
                currentBoard = chunks.get(chunks.size() - 1);
                boardPanel.repaint();
            }
        }.execute();
    }

    private void saveSolution() {
        if (solution == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (!filename.endsWith(".txt")) {
                filename += ".txt";
            }
            FileHandler.writeSolution(filename, solution.toString(), executionTime, iterations);
            JOptionPane.showMessageDialog(this, 
                "Solusi berhasil disimpan!",
                "Berhasil",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void drawBoard(Graphics g) {
        Board boardToDraw = currentBoard != null ? currentBoard : 
                          (solution != null ? solution : null);
        
        if (boardToDraw == null && input == null) return;

        int rows = input != null ? input.rows : boardToDraw.getRows();
        int cols = input != null ? input.cols : boardToDraw.getCols();
        
        int cellSize = Math.min(boardPanel.getWidth() / cols, 
                              boardPanel.getHeight() / rows);
        
        // Draw grid
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= rows; i++) {
            g.drawLine(0, i * cellSize, cols * cellSize, i * cellSize);
        }
        for (int j = 0; j <= cols; j++) {
            g.drawLine(j * cellSize, 0, j * cellSize, rows * cellSize);
        }

        // Draw pieces
        if (boardToDraw != null) {
            char[][] grid = boardToDraw.getGrid();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char piece = grid[i][j];
                    if (piece != '.') {
                        int colorIndex = piece - 'A';
                        g.setColor(pieceColors[colorIndex]);
                        g.fillRect(j * cellSize + 1, i * cellSize + 1, 
                                 cellSize - 1, cellSize - 1);
                        
                        g.setColor(Color.BLACK);
                        g.setFont(new Font("Arial", Font.BOLD, cellSize / 2));
                        FontMetrics fm = g.getFontMetrics();
                        String text = String.valueOf(piece);
                        int textX = j * cellSize + (cellSize - fm.stringWidth(text)) / 2;
                        int textY = i * cellSize + (cellSize + fm.getAscent()) / 2;
                        g.drawString(text, textX, textY);
                    }
                }
            }
        }
    }

    private void saveAsImage() {
        if (solution == null) return;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".png") 
                    || f.getName().toLowerCase().endsWith(".jpg");
            }
            public String getDescription() {
                return "File gambar (*.png, *.jpg)";
            }
        });

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (!filename.toLowerCase().endsWith(".png") && !filename.toLowerCase().endsWith(".jpg")) {
                filename += ".png";
            }

            try {
                int scale = 50; 
                int rows = solution.getRows();
                int cols = solution.getCols();
                BufferedImage image = new BufferedImage(cols * scale, rows * scale, 
                                                      BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();

               
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, cols * scale, rows * scale);

               
                g2d.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i <= rows; i++) {
                    g2d.drawLine(0, i * scale, cols * scale, i * scale);
                }
                for (int j = 0; j <= cols; j++) {
                    g2d.drawLine(j * scale, 0, j * scale, rows * scale);
                }

                // Draw pieces
                char[][] grid = solution.getGrid();
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        char piece = grid[i][j];
                        if (piece != '.') {
                            int colorIndex = piece - 'A';
                            g2d.setColor(pieceColors[colorIndex]);
                            g2d.fillRect(j * scale + 1, i * scale + 1, scale - 1, scale - 1);
                            
                            g2d.setColor(Color.BLACK);
                            g2d.setFont(new Font("Arial", Font.BOLD, scale / 2));
                            FontMetrics fm = g2d.getFontMetrics();
                            String text = String.valueOf(piece);
                            int textX = j * scale + (scale - fm.stringWidth(text)) / 2;
                            int textY = i * scale + (scale + fm.getAscent()) / 2;
                            g2d.drawString(text, textX, textY);
                        }
                    }
                }

                g2d.dispose();

                // Save image
                String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
                ImageIO.write(image, extension, new File(filename));

                JOptionPane.showMessageDialog(this, 
                    "Gambar berhasil disimpan",
                    "Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan gambar: " + ex.getMessage(),
                    "Gagal",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}