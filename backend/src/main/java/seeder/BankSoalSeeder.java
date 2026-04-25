package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;
import java.time.Instant;

public class BankSoalSeeder {

    private static final TableName TABLE_BANK_SOAL = TableName.valueOf("bankSoal");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "BS001", "SU001", "UTS Pemrograman Dasar", "Struktur kontrol perulangan yang benar adalah...",
                "10", "PG", "TX001", "THN001", "SMT001", "KLS001", "SEA001", "SUB001", "RPD001-1", "SP001");

        seedOne(client, "BS002", "SU002", "UTS Pemrograman Dasar", "Pilih karakteristik algoritma yang baik",
                "15", "MULTI", "TX002", "THN001", "SMT001", "KLS001", "SEA002", "SUB002", "RPD002-2", "SP001");

        seedOne(client, "BS003", "SU003", "UTS Pemrograman Dasar", "Cocokkan istilah dengan definisi",
                "20", "COCOK", "TX002", "THN001", "SMT002", "KLS003", "SEA003", "SUB002", "RPD003-3", "SP001");

        seedOne(client, "BS004", "SU004", "UTS Pemrograman Dasar", "Kepanjangan HTTP adalah...",
                "5", "ISIAN", "TX001", "THN001", "SMT002", "KLS003", "SEA003", "SUB001", "RPD004-4", "SP001");

        seedOne(client, "BS005", "SU005", "UTS Sistem Informasi",
                "Diagram alur proses bisnis paling tepat adalah...", "10", "PG", "TX101", "THN002", "SMT003",
                "KLS005", "SEA004", "SUB003", "RPD005-1", "SP002");

        seedOne(client, "BS006", "SU006", "UTS Sistem Informasi", "Pilih komponen utama dashboard bisnis",
                "15", "MULTI", "TX102", "THN002", "SMT003", "KLS005", "SEA004", "SUB004", "RPD006-2", "SP002");

        seedOne(client, "BS007", "SU007", "UTS Sistem Informasi", "Cocokkan KPI dengan definisinya",
                "20", "COCOK", "TX103", "THN002", "SMT004", "KLS007", "SEA005", "SUB003", "RPD007-3", "SP002");

        seedOne(client, "BS008", "SU008", "UTS Sistem Informasi", "Kepanjangan KPI adalah...",
                "5", "ISIAN", "TX101", "THN002", "SMT004", "KLS007", "SEA005", "SUB004", "RPD008-4", "SP002");

        seedOne(client, "BS009", "SU009", "UTS Jaringan Dasar", "Perangkat yang menghubungkan jaringan adalah...",
                "10", "PG", "TX001", "THN001", "SMT001", "KLS001", "SEA001", "SUB001", "RPD009-5", "SP001");

        seedOne(client, "BS010", "SU010", "UTS Desain Antarmuka", "Pilih prinsip UI yang baik",
                "15", "MULTI", "TX002", "THN001", "SMT001", "KLS002", "SEA002", "SUB002", "RPD010-6", "SP001");

        seedOne(client, "BS011", "SU011", "UTS Kecerdasan Bisnis", "Cocokkan konsep dengan contoh",
                "20", "COCOK", "TX103", "THN002", "SMT004", "KLS007", "SEA005", "SUB003", "RPD011-5", "SP002");

        seedOne(client, "BS012", "SU012", "UTS Manajemen Proyek TI", "Kepanjangan SDLC adalah...",
                "5", "ISIAN", "TX101", "THN002", "SMT003", "KLS005", "SEA004", "SUB004", "RPD012-6", "SP002");

        seedOne(client, "BS013", "SU013", "UTS Keamanan Jaringan", "Firewall berfungsi untuk...",
                "10", "PG", "TX001", "THN001", "SMT001", "KLS001", "SEA001", "SUB001", "RPD013-7", "SP001");

        seedOne(client, "BS014", "SU014", "UTS Interaksi Manusia Komputer", "Pilih aspek UX yang penting",
                "15", "MULTI", "TX002", "THN001", "SMT001", "KLS002", "SEA002", "SUB002", "RPD014-8", "SP001");

        seedOne(client, "BS015", "SU015", "UTS Sistem Pendukung Keputusan", "Cocokkan metode dengan kegunaannya",
                "20", "COCOK", "TX103", "THN002", "SMT004", "KLS007", "SEA005", "SUB003", "RPD015-7", "SP002");

        seedOne(client, "BS016", "SU016", "UTS Manajemen Basis Data Bisnis", "Kepanjangan DBMS adalah...",
                "5", "ISIAN", "TX101", "THN002", "SMT003", "KLS005", "SEA004", "SUB004", "RPD016-8", "SP002");
    }

    private void seedOne(HBaseCustomClient client, String idBankSoal, String idSoalUjian, String namaUjian,
            String pertanyaan, String bobot, String jenisSoal, String taksonomiId, String tahunId, String semesterId,
            String kelasId, String seasonId, String subjectId, String rpsDetailId, String studyProgramId)
            throws IOException {

        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "idBankSoal", idBankSoal);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "idSoalUjian", idSoalUjian);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "namaUjian", namaUjian);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "pertanyaan", pertanyaan);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "bobot", bobot);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "jenisSoal", jenisSoal);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "createdAt", Instant.now().toString());

        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "soalUjian", "idSoalUjian", idSoalUjian);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "soalUjian", "namaUjian", namaUjian);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "soalUjian", "pertanyaan", pertanyaan);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "soalUjian", "bobot", bobot);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "soalUjian", "jenisSoal", jenisSoal);

        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "study_program", "id", studyProgramId);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "study_program", "name", studyProgramNameById(studyProgramId));

        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "taksonomi", "idTaksonomi", taksonomiId);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "taksonomi", "namaTaksonomi", taksonomiNameById(taksonomiId));

        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "tahunAjaran", "idTahun", tahunId);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "tahunAjaran", "tahunAjaran", "2025/2026");

        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "semester", "idSemester", semesterId);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "semester", "namaSemester", semesterNameById(semesterId));

        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "kelas", "idKelas", kelasId);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "kelas", "namaKelas", kelasNameById(kelasId));

        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "seasons", "idSeason", seasonId);

        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "subject", "id", subjectId);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "subject", "name", subjectNameById(subjectId));

        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "rps_detail", "id", rpsDetailId);
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "rps_detail", "week", weekByRpsDetailId(rpsDetailId));
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "rps_detail", "sub_cp_mk", subCpMkByRpsDetailId(rpsDetailId));
        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "rps_detail", "weight", "25");

        if ("PG".equals(jenisSoal)) {
            client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "opsi",
                    "{\"A\":\"if\",\"B\":\"for\",\"C\":\"return\",\"D\":\"switch\"}");
            client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "jawabanBenar", "[\"B\"]");
        } else if ("MULTI".equals(jenisSoal)) {
            client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "opsi",
                    "{\"A\":\"Jelas\",\"B\":\"Efisien\",\"C\":\"Tidak berhingga\",\"D\":\"Punya input output\"}");
            client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "jawabanBenar", "[\"A\",\"B\",\"D\"]");
        } else if ("COCOK".equals(jenisSoal)) {
            client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "pasangan",
                    "{\"Primary Key\":\"Kunci unik\",\"Foreign Key\":\"Relasi antar tabel\"}");
            client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "jawabanBenar",
                    "[\"Primary Key=Kunci unik\",\"Foreign Key=Relasi antar tabel\"]");
        } else if ("ISIAN".equals(jenisSoal)) {
            client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "jawabanBenar",
                    "[\"Hypertext Transfer Protocol\"]");
            client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "main", "toleransiTypo", "1");
        }

        client.insertRecord(TABLE_BANK_SOAL, idBankSoal, "detail", "created_by", "Seeder");
    }

    private String subjectNameById(String subjectId) {
        if ("SUB001".equals(subjectId)) {
            return "Pemrograman Dasar";
        }
        if ("SUB002".equals(subjectId)) {
            return "Algoritma dan Struktur Data";
        }
        if ("SUB003".equals(subjectId)) {
            return "Sistem Informasi Manajemen";
        }
        if ("SUB004".equals(subjectId)) {
            return "Analitik Data Bisnis";
        }
        return "Subject";
    }

    private String kelasNameById(String kelasId) {
        if ("KLS001".equals(kelasId) || "KLS005".equals(kelasId)) {
            return "1A";
        }
        if ("KLS002".equals(kelasId) || "KLS006".equals(kelasId)) {
            return "1B";
        }
        if ("KLS003".equals(kelasId) || "KLS007".equals(kelasId)) {
            return "2A";
        }
        if ("KLS004".equals(kelasId) || "KLS008".equals(kelasId)) {
            return "2B";
        }
        return "Kelas";
    }

    private String semesterNameById(String semesterId) {
        if ("SMT001".equals(semesterId) || "SMT003".equals(semesterId)) {
            return "Semester 1";
        }
        if ("SMT002".equals(semesterId) || "SMT004".equals(semesterId)) {
            return "Semester 2";
        }
        return "Semester";
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

    private String studyProgramNameById(String studyProgramId) {
        if ("SP001".equals(studyProgramId)) {
            return "Teknik Informatika";
        }
        if ("SP002".equals(studyProgramId)) {
            return "Sistem Informasi Bisnis";
        }
        return "Program Studi";
    }

    private String weekByRpsDetailId(String rpsDetailId) {
        if ("RPD001-1".equals(rpsDetailId)) {
            return "1";
        }
        if ("RPD002-2".equals(rpsDetailId)) {
            return "2";
        }
        if ("RPD003-3".equals(rpsDetailId)) {
            return "3";
        }
        if ("RPD004-4".equals(rpsDetailId)) {
            return "4";
        }
        if ("RPD005-1".equals(rpsDetailId)) {
            return "1";
        }
        if ("RPD006-2".equals(rpsDetailId)) {
            return "2";
        }
        if ("RPD007-3".equals(rpsDetailId)) {
            return "3";
        }
        if ("RPD008-4".equals(rpsDetailId)) {
            return "4";
        }
        if ("RPD009-5".equals(rpsDetailId)) {
            return "5";
        }
        if ("RPD010-6".equals(rpsDetailId)) {
            return "6";
        }
        if ("RPD011-5".equals(rpsDetailId)) {
            return "5";
        }
        if ("RPD012-6".equals(rpsDetailId)) {
            return "6";
        }
        if ("RPD013-7".equals(rpsDetailId)) {
            return "7";
        }
        if ("RPD014-8".equals(rpsDetailId)) {
            return "8";
        }
        if ("RPD015-7".equals(rpsDetailId)) {
            return "7";
        }
        if ("RPD016-8".equals(rpsDetailId)) {
            return "8";
        }
        return "1";
    }

    private String subCpMkByRpsDetailId(String rpsDetailId) {
        if ("RPD001-1".equals(rpsDetailId)) {
            return "CP MK 1";
        }
        if ("RPD002-2".equals(rpsDetailId)) {
            return "CP MK 2";
        }
        if ("RPD003-3".equals(rpsDetailId)) {
            return "CP MK 3";
        }
        if ("RPD004-4".equals(rpsDetailId)) {
            return "CP MK 4";
        }
        if ("RPD005-1".equals(rpsDetailId)) {
            return "CP MK 5";
        }
        if ("RPD006-2".equals(rpsDetailId)) {
            return "CP MK 6";
        }
        if ("RPD007-3".equals(rpsDetailId)) {
            return "CP MK 7";
        }
        if ("RPD008-4".equals(rpsDetailId)) {
            return "CP MK 8";
        }
        if ("RPD009-5".equals(rpsDetailId)) {
            return "CP MK 9";
        }
        if ("RPD010-6".equals(rpsDetailId)) {
            return "CP MK 10";
        }
        if ("RPD011-5".equals(rpsDetailId)) {
            return "CP MK 11";
        }
        if ("RPD012-6".equals(rpsDetailId)) {
            return "CP MK 12";
        }
        if ("RPD013-7".equals(rpsDetailId)) {
            return "CP MK 13";
        }
        if ("RPD014-8".equals(rpsDetailId)) {
            return "CP MK 14";
        }
        if ("RPD015-7".equals(rpsDetailId)) {
            return "CP MK 15";
        }
        if ("RPD016-8".equals(rpsDetailId)) {
            return "CP MK 16";
        }
        return "CP MK";
    }
}
