package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class Season2Seeder extends Seeder2Support {
    private static final TableName TABLE = TableName.valueOf("seasons");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "SEA001", SP_TI_ID, SP_TI_NAME, "KLS001", "1A", "THN001", "2025/2026", "SMT001",
                "Semester 1", "LEC001", "Budi Raharjo", "19880001",
                new String[][] { { "STU001", "2351507001", "Andi Pratama", "Laki-laki", "0811111111" },
                        { "STU002", "2351507002", "Citra Lestari", "Perempuan", "0811111112" } },
                new String[][] { { "SUB001", "Pemrograman Dasar" }, { "SUB002", "Algoritma dan Struktur Data" } });

        seedOne(client, "SEA002", SP_SIB_ID, SP_SIB_NAME, "KLS003", "1A", "THN002", "2025/2026", "SMT003",
                "Semester 1", "LEC003", "Fajar Hidayat", "19900003",
                new String[][] { { "STU003", "2351508001", "Dian Puspita", "Perempuan", "0811111113" },
                        { "STU004", "2351508002", "Galih Maulana", "Laki-laki", "0811111114" } },
                new String[][] { { "SUB003", "Sistem Informasi Manajemen" }, { "SUB004", "Analitik Data Bisnis" } });
    }

    private void seedOne(HBaseCustomClient client, String id, String spId, String spName, String kelasId,
            String kelasName, String tahunId, String tahunName, String semesterId, String semesterName,
            String lectureId, String lectureName, String lectureNip, String[][] students, String[][] subjects)
            throws IOException {
        put(client, TABLE, id, "main", "idSeason", id);
        studyProgram(client, TABLE, id, "study_program", spId, spName);
        put(client, TABLE, id, "kelas", "idKelas", kelasId);
        put(client, TABLE, id, "kelas", "namaKelas", kelasName);
        put(client, TABLE, id, "tahunAjaran", "idTahun", tahunId);
        put(client, TABLE, id, "tahunAjaran", "tahunAjaran", tahunName);
        put(client, TABLE, id, "semester", "idSemester", semesterId);
        put(client, TABLE, id, "semester", "namaSemester", semesterName);
        put(client, TABLE, id, "lecture", "id", lectureId);
        put(client, TABLE, id, "lecture", "name", lectureName);
        put(client, TABLE, id, "lecture", "nip", lectureNip);

        for (int i = 0; i < students.length; i++) {
            put(client, TABLE, id, "student", "id" + i, students[i][0]);
            put(client, TABLE, id, "student", "nisn" + i, students[i][1]);
            put(client, TABLE, id, "student", "name" + i, students[i][2]);
            put(client, TABLE, id, "student", "gender" + i, students[i][3]);
            put(client, TABLE, id, "student", "phone" + i, students[i][4]);
        }

        for (int i = 0; i < subjects.length; i++) {
            put(client, TABLE, id, "subject", "id" + i, subjects[i][0]);
            put(client, TABLE, id, "subject", "name" + i, subjects[i][1]);
        }

        put(client, TABLE, id, "detail", "created_by", CREATED_BY);
    }
}
