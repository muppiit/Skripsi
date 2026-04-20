package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class SeasonSeeder {

    private static final TableName TABLE_SEASON = TableName.valueOf("seasons");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "SEA001", "SP001", "Teknik Informatika", "KLS001", "1A", "THN001", "2024/2025",
                "SMT001", "Semester 1", "LEC001", "Dosen Pengampu", "19880001",
                new String[][] { { "STU001", "NISN000001", "Andi Pratama", "L", "0811111111" },
                        { "STU002", "NISN000002", "Budi Santoso", "L", "0811111112" } },
                new String[][] { { "SUB001", "Pemrograman Dasar" } });

        seedOne(client, "SEA002", "SP001", "Teknik Informatika", "KLS001", "1A", "THN001", "2024/2025",
                "SMT001", "Semester 1", "LEC001", "Dosen Pengampu", "19880001",
                new String[][] { { "STU001", "NISN000001", "Andi Pratama", "L", "0811111111" } },
                new String[][] { { "SUB002", "Algoritma dan Struktur Data" } });

        seedOne(client, "SEA003", "SP001", "Teknik Informatika", "KLS003", "2A", "THN001", "2024/2025",
                "SMT002", "Semester 2", "LEC001", "Dosen Pengampu", "19880001",
                new String[][] { { "STU003", "NISN000003", "Citra Lestari", "P", "0811111113" } },
                new String[][] { { "SUB002", "Algoritma dan Struktur Data" } });

        seedOne(client, "SEA004", "SP002", "Sistem Informasi Bisnis", "KLS005", "1A", "THN002", "2024/2025",
                "SMT003", "Semester 1", "LEC001", "Dosen Pengampu", "19880001",
                new String[][] { { "STU004", "NISN000004", "Dian Puspita", "P", "0811111114" } },
                new String[][] { { "SUB003", "Sistem Informasi Manajemen" } });

        seedOne(client, "SEA005", "SP002", "Sistem Informasi Bisnis", "KLS007", "2A", "THN002", "2024/2025",
                "SMT004", "Semester 2", "LEC001", "Dosen Pengampu", "19880001",
                new String[][] { { "STU004", "NISN000004", "Dian Puspita", "P", "0811111114" } },
                new String[][] { { "SUB004", "Analitik Data Bisnis" } });
    }

    private void seedOne(HBaseCustomClient client, String idSeason,
            String studyProgramId, String studyProgramName,
            String kelasId, String namaKelas,
            String idTahun, String tahunAjaran,
            String idSemester, String namaSemester,
            String lectureId, String lectureName, String lectureNip,
            String[][] students, String[][] subjects) throws IOException {

        client.insertRecord(TABLE_SEASON, idSeason, "main", "idSeason", idSeason);

        client.insertRecord(TABLE_SEASON, idSeason, "study_program", "id", studyProgramId);
        client.insertRecord(TABLE_SEASON, idSeason, "study_program", "name", studyProgramName);

        client.insertRecord(TABLE_SEASON, idSeason, "kelas", "idKelas", kelasId);
        client.insertRecord(TABLE_SEASON, idSeason, "kelas", "namaKelas", namaKelas);

        client.insertRecord(TABLE_SEASON, idSeason, "tahunAjaran", "idTahun", idTahun);
        client.insertRecord(TABLE_SEASON, idSeason, "tahunAjaran", "tahunAjaran", tahunAjaran);

        client.insertRecord(TABLE_SEASON, idSeason, "semester", "idSemester", idSemester);
        client.insertRecord(TABLE_SEASON, idSeason, "semester", "namaSemester", namaSemester);

        client.insertRecord(TABLE_SEASON, idSeason, "lecture", "id", lectureId);
        client.insertRecord(TABLE_SEASON, idSeason, "lecture", "name", lectureName);
        client.insertRecord(TABLE_SEASON, idSeason, "lecture", "nip", lectureNip);

        for (int i = 0; i < students.length; i++) {
            client.insertRecord(TABLE_SEASON, idSeason, "student", "id" + i, students[i][0]);
            client.insertRecord(TABLE_SEASON, idSeason, "student", "nisn" + i, students[i][1]);
            client.insertRecord(TABLE_SEASON, idSeason, "student", "name" + i, students[i][2]);
            client.insertRecord(TABLE_SEASON, idSeason, "student", "gender" + i, students[i][3]);
            client.insertRecord(TABLE_SEASON, idSeason, "student", "phone" + i, students[i][4]);
        }

        for (int i = 0; i < subjects.length; i++) {
            client.insertRecord(TABLE_SEASON, idSeason, "subject", "id" + i, subjects[i][0]);
            client.insertRecord(TABLE_SEASON, idSeason, "subject", "name" + i, subjects[i][1]);
        }

        client.insertRecord(TABLE_SEASON, idSeason, "detail", "created_by", "Seeder");
    }
}
