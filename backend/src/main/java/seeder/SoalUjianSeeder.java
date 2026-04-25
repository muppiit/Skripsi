package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;
import java.time.Instant;

public class SoalUjianSeeder {

        private static final TableName TABLE_SOAL_UJIAN = TableName.valueOf("soalUjian");

        public void seed(HBaseCustomClient client) throws IOException {
                seedPg(client, "SU001", "UTS Pemrograman Dasar", "Struktur kontrol perulangan yang benar adalah...",
                                "10",
                                "TX001", "SP001", "Teknik Informatika", "USR004", "Dosen TI Polinema Lumajang");
                seedMulti(client, "SU002", "UTS Pemrograman Dasar", "Pilih karakteristik algoritma yang baik", "15",
                                "TX002", "SP001", "Teknik Informatika", "USR009", "Ratna Wulandari");
                seedCocok(client, "SU003", "UTS Pemrograman Dasar", "Cocokkan istilah dengan definisi", "20", "TX003",
                                "SP001", "Teknik Informatika", "USR004", "Dosen TI Polinema Lumajang");
                seedIsian(client, "SU004", "UTS Pemrograman Dasar", "Kepanjangan HTTP adalah...", "5", "TX001", "1",
                                "SP001", "Teknik Informatika", "USR009", "Ratna Wulandari");

                seedPg(client, "SU005", "UTS Sistem Informasi", "Diagram alur proses bisnis paling tepat adalah...",
                                "10",
                                "TX101", "SP002", "Sistem Informasi Bisnis", "USR010", "Fajar Hidayat");
                seedMulti(client, "SU006", "UTS Sistem Informasi", "Pilih komponen utama dashboard bisnis", "15",
                                "TX102",
                                "SP002", "Sistem Informasi Bisnis", "USR011", "Sari Ayu");
                seedCocok(client, "SU007", "UTS Sistem Informasi", "Cocokkan KPI dengan definisinya", "20", "TX103",
                                "SP002", "Sistem Informasi Bisnis", "USR010", "Fajar Hidayat");
                seedIsian(client, "SU008", "UTS Sistem Informasi", "Kepanjangan KPI adalah...", "5", "TX101", "1",
                                "SP002",
                                "Sistem Informasi Bisnis", "USR011", "Sari Ayu");

                seedPg(client, "SU009", "UTS Jaringan Dasar", "Perangkat yang menghubungkan jaringan adalah...",
                                "10", "TX001", "SP001", "Teknik Informatika", "USR004",
                                "Dosen TI Polinema Lumajang");
                seedMulti(client, "SU010", "UTS Desain Antarmuka", "Pilih prinsip UI yang baik", "15", "TX002",
                                "SP001", "Teknik Informatika", "USR009", "Ratna Wulandari");
                seedCocok(client, "SU011", "UTS Kecerdasan Bisnis", "Cocokkan konsep dengan contoh", "20",
                                "TX103", "SP002", "Sistem Informasi Bisnis", "USR010", "Fajar Hidayat");
                seedIsian(client, "SU012", "UTS Manajemen Proyek TI", "Kepanjangan SDLC adalah...", "5", "TX101",
                                "1", "SP002", "Sistem Informasi Bisnis", "USR011", "Sari Ayu");

                seedPg(client, "SU013", "UTS Keamanan Jaringan", "Firewall berfungsi untuk...", "10", "TX001",
                                "SP001", "Teknik Informatika", "USR004", "Dosen TI Polinema Lumajang");
                seedMulti(client, "SU014", "UTS Interaksi Manusia Komputer", "Pilih aspek UX yang penting", "15",
                                "TX002", "SP001", "Teknik Informatika", "USR009", "Ratna Wulandari");
                seedCocok(client, "SU015", "UTS Sistem Pendukung Keputusan", "Cocokkan metode dengan kegunaannya",
                                "20", "TX103", "SP002", "Sistem Informasi Bisnis", "USR010",
                                "Fajar Hidayat");
                seedIsian(client, "SU016", "UTS Manajemen Basis Data Bisnis", "Kepanjangan DBMS adalah...", "5",
                                "TX101", "1", "SP002", "Sistem Informasi Bisnis", "USR011",
                                "Sari Ayu");
        }

        private void seedBase(HBaseCustomClient client, String id, String namaUjian, String pertanyaan, String bobot,
                        String jenisSoal, String taksonomiId, String studyProgramId, String studyProgramName,
                        String userId, String userName) throws IOException {
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "idSoalUjian", id);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "namaUjian", namaUjian);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "pertanyaan", pertanyaan);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "bobot", bobot);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "jenisSoal", jenisSoal);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "createdAt", Instant.now().toString());

                client.insertRecord(TABLE_SOAL_UJIAN, id, "user", "id", userId);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "user", "name", userName);

                client.insertRecord(TABLE_SOAL_UJIAN, id, "taksonomi", "idTaksonomi", taksonomiId);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "taksonomi", "namaTaksonomi", taksonomiNameById(taksonomiId));

                client.insertRecord(TABLE_SOAL_UJIAN, id, "study_program", "idSchool", studyProgramId);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "study_program", "nameSchool", studyProgramName);

                client.insertRecord(TABLE_SOAL_UJIAN, id, "detail", "created_by", "Seeder");
        }

        private void seedPg(HBaseCustomClient client, String id, String namaUjian, String pertanyaan, String bobot,
                        String taksonomiId, String studyProgramId, String studyProgramName,
                        String userId, String userName) throws IOException {
                seedBase(client, id, namaUjian, pertanyaan, bobot, "PG", taksonomiId, studyProgramId, studyProgramName,
                                userId, userName);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "opsi",
                                "{\"A\":\"if\",\"B\":\"for\",\"C\":\"return\",\"D\":\"switch\"}");
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "jawabanBenar", "[\"B\"]");
        }

        private void seedMulti(HBaseCustomClient client, String id, String namaUjian, String pertanyaan, String bobot,
                        String taksonomiId, String studyProgramId, String studyProgramName,
                        String userId, String userName) throws IOException {
                seedBase(client, id, namaUjian, pertanyaan, bobot, "MULTI", taksonomiId, studyProgramId,
                                studyProgramName,
                                userId, userName);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "opsi",
                                "{\"A\":\"Jelas\",\"B\":\"Efisien\",\"C\":\"Tidak berhingga\",\"D\":\"Punya input output\"}");
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "jawabanBenar", "[\"A\",\"B\",\"D\"]");
        }

        private void seedCocok(HBaseCustomClient client, String id, String namaUjian, String pertanyaan, String bobot,
                        String taksonomiId, String studyProgramId, String studyProgramName,
                        String userId, String userName) throws IOException {
                seedBase(client, id, namaUjian, pertanyaan, bobot, "COCOK", taksonomiId, studyProgramId,
                                studyProgramName,
                                userId, userName);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "pasangan",
                                "{\"Primary Key\":\"Kunci unik\",\"Foreign Key\":\"Relasi antar tabel\"}");
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "jawabanBenar",
                                "[\"Primary Key=Kunci unik\",\"Foreign Key=Relasi antar tabel\"]");
        }

        private void seedIsian(HBaseCustomClient client, String id, String namaUjian, String pertanyaan, String bobot,
                        String taksonomiId, String toleransiTypo, String studyProgramId, String studyProgramName,
                        String userId, String userName)
                        throws IOException {
                seedBase(client, id, namaUjian, pertanyaan, bobot, "ISIAN", taksonomiId, studyProgramId,
                                studyProgramName,
                                userId, userName);
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "jawabanBenar", "[\"Hypertext Transfer Protocol\"]");
                client.insertRecord(TABLE_SOAL_UJIAN, id, "main", "toleransiTypo", toleransiTypo);
        }

        private String taksonomiNameById(String taksonomiId) {
                if ("TX001".equals(taksonomiId) || "TX101".equals(taksonomiId)) {
                        return "C1";
                }
                if ("TX002".equals(taksonomiId) || "TX102".equals(taksonomiId)) {
                        return "C2";
                }
                if ("TX003".equals(taksonomiId) || "TX103".equals(taksonomiId)) {
                        return "C3";
                }
                return "C1";
        }
}
