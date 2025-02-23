# IQ Puzzler Pro Solver - Tucil1 Strategi Algoritma (IF2211)

## Deskripsi Program

Program ini merupakan implementasi penyelesaian permainan IQ Puzzler Pro menggunakan algoritma Brute Force murni. IQ Puzzler Pro adalah permainan papan yang diproduksi oleh Smart Games di mana pemain harus mengisi seluruh papan dengan blok-blok puzzle yang tersedia. Program ini ditulis dalam bahasa Java dan memiliki dua mode operasi: Command Line Interface (CLI) dan Graphical User Interface (GUI).

## Fitur Program
- Implementasi algoritma Brute Force murni
- Visualisasi penempatan blok puzzle berwarna
- Animasi proses pencarian solusi (pada mode GUI)
- Penyimpanan solusi dalam format teks
- Penyimpanan solusi dalam format gambar (pada mode GUI)
- Mendukung konfigurasi puzzle DEFAULT

## Requirement Program
1. Java Development Kit (JDK) versi 8 atau lebih tinggi
2. Java Runtime Environment (JRE)

## Cara Kompilasi dan Menjalankan Program
Untuk menjalankan program ini, ikuti langkah berikut:
1. Lakukan cloning repository(jika belum) dengan cara menjalankan perintah berikut pada terminal:
   ```bash
   git clone https://github.com/fliegenhaan/Tucil1_13523124.git
   ```
3. Lalu masuk ke repositori program
4. Untuk mengkompilasi, jalankan:
   ```bash
   javac -d bin src/main/Main.java src/main/*.java src/model/*.java src/solver/*.java src/utils/*.java src/config/*.java
   ```
5. Lalu:
   ```bash
   java -cp bin main.Main
   ```
6. Pilih mode (1. GUI, 2. CLI)
   Jika GUI maka lakukan puzzle solving, uploading file, dan saving file dengan button yang tersedia pada interface dalam bahasa Indonesia:
   a. Klik tombol "Pilih file input" untuk memilih file test case
   b. Klik "Selesaikan Puzzle" untuk mencari solusi
   c. Gunakan slider untuk mengatur kecepatan animasi
   d. Klik "Simpan Solusi" untuk menyimpan dalam format teks
   e. Klik "Simpan sebagai gambar" untuk menyimpan visualisasi dalam format gambar
   Jika CLI maka masukkan file input dengan cara:
   a. Pastikan file input berada dalam folder test
   b. Lalu jawab pertanyaan "Masukkan nama file input (.txt): " dengan test\[nama file txt].txt cth:
   ```bash
   Masukkan nama file input (.txt): test\test6.txt
   ```
   c. Ikuti perintah selanjutnya

## Struktur Repository

```
Tucil1_13523124/
├── bin/                    # Berisi file .class hasil kompilasi
├── src/                    # Source code program
│   ├── config/            # Konfigurasi program
│   ├── main/              # Main program dan GUI
│   ├── model/             # Model data untuk Board, Piece, dan Position
│   ├── solver/            # Implementasi algoritma solver
│   └── utils/             # Utility functions
├── test/                  # File test cases
├── doc/                   # Dokumentasi dan laporan
└── README.md              # Dokumentasi penggunaan program    
```

## Developer

**Muhammad Raihaan Perdana**  
Teknik Informatika - Institut Teknologi Bandung  
NIM: 13523124 - K03
