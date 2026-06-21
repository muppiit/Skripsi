package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class Academic2Seeder extends Seeder2Support {
    private static final TableName TAHUN_AJARAN = TableName.valueOf("tahunAjaran");
    private static final TableName SEMESTER = TableName.valueOf("semester");
    private static final TableName KELAS = TableName.valueOf("kelas");
    private static final TableName SUBJECT = TableName.valueOf("subjects");
    private static final TableName TAKSONOMI = TableName.valueOf("taksonomi");

    public void seed(HBaseCustomClient client) throws IOException {
        seedTahunAjaran(client);
        seedSemester(client);
        seedKelas(client);
        seedSubject(client);
        seedTaksonomi(client);
    }

    private void seedTahunAjaran(HBaseCustomClient client) throws IOException {
        seedTahun(client, "THN001", "2025/2026", SP_TI_ID, SP_TI_NAME);
        seedTahun(client, "THN002", "2025/2026", SP_SIB_ID, SP_SIB_NAME);
    }

    private void seedTahun(HBaseCustomClient client, String id, String tahun, String spId, String spName)
            throws IOException {
        put(client, TAHUN_AJARAN, id, "main", "idTahun", id);
        put(client, TAHUN_AJARAN, id, "main", "tahunAjaran", tahun);
        studyProgram(client, TAHUN_AJARAN, id, "study_program", spId, spName);
        put(client, TAHUN_AJARAN, id, "detail", "created_by", CREATED_BY);
    }

    private void seedSemester(HBaseCustomClient client) throws IOException {
        seedSemesterOne(client, "SMT001", "Semester 1", SP_TI_ID, SP_TI_NAME);
        seedSemesterOne(client, "SMT002", "Semester 2", SP_TI_ID, SP_TI_NAME);
        seedSemesterOne(client, "SMT003", "Semester 1", SP_SIB_ID, SP_SIB_NAME);
        seedSemesterOne(client, "SMT004", "Semester 2", SP_SIB_ID, SP_SIB_NAME);
    }

    private void seedSemesterOne(HBaseCustomClient client, String id, String name, String spId, String spName)
            throws IOException {
        put(client, SEMESTER, id, "main", "idSemester", id);
        put(client, SEMESTER, id, "main", "namaSemester", name);
        studyProgram(client, SEMESTER, id, "study_program", spId, spName);
        put(client, SEMESTER, id, "detail", "created_by", CREATED_BY);
    }

    private void seedKelas(HBaseCustomClient client) throws IOException {
        seedKelasOne(client, "KLS001", "1A", SP_TI_ID, SP_TI_NAME, "THN001", "2025/2026");
        seedKelasOne(client, "KLS002", "1B", SP_TI_ID, SP_TI_NAME, "THN001", "2025/2026");
        seedKelasOne(client, "KLS003", "1A", SP_SIB_ID, SP_SIB_NAME, "THN002", "2025/2026");
        seedKelasOne(client, "KLS004", "1B", SP_SIB_ID, SP_SIB_NAME, "THN002", "2025/2026");
    }

    private void seedKelasOne(HBaseCustomClient client, String id, String name, String spId, String spName,
            String tahunId, String tahunName) throws IOException {
        put(client, KELAS, id, "main", "idKelas", id);
        put(client, KELAS, id, "main", "namaKelas", name);
        studyProgram(client, KELAS, id, "study_program", spId, spName);
        put(client, KELAS, id, "tahunAjaran", "idTahun", tahunId);
        put(client, KELAS, id, "tahunAjaran", "tahunAjaran", tahunName);
        put(client, KELAS, id, "detail", "created_by", CREATED_BY);
    }

    private void seedSubject(HBaseCustomClient client) throws IOException {
        seedSubjectOne(client, "SUB001", "Pemrograman Dasar", "Dasar logika dan pemrograman", "3", "2025",
                SP_TI_ID, SP_TI_NAME, "SG001", "Mata Kuliah Dasar");
        seedSubjectOne(client, "SUB002", "Algoritma dan Struktur Data", "Algoritma dan struktur data dasar", "3",
                "2025", SP_TI_ID, SP_TI_NAME, "SG002", "Mata Kuliah Keahlian");
        seedSubjectOne(client, "SUB003", "Sistem Informasi Manajemen", "Konsep sistem informasi untuk bisnis", "3",
                "2025", SP_SIB_ID, SP_SIB_NAME, "SG001", "Mata Kuliah Dasar");
        seedSubjectOne(client, "SUB004", "Analitik Data Bisnis", "Analisis data untuk pengambilan keputusan", "3",
                "2025", SP_SIB_ID, SP_SIB_NAME, "SG002", "Mata Kuliah Keahlian");
    }

    private void seedSubjectOne(HBaseCustomClient client, String id, String name, String description,
            String creditPoint, String yearCommenced, String spId, String spName, String groupId, String groupName)
            throws IOException {
        put(client, SUBJECT, id, "main", "id", id);
        put(client, SUBJECT, id, "main", "name", name);
        put(client, SUBJECT, id, "main", "description", description);
        put(client, SUBJECT, id, "main", "credit_point", creditPoint);
        put(client, SUBJECT, id, "main", "year_commenced", yearCommenced);
        studyProgram(client, SUBJECT, id, "study_program", spId, spName);
        put(client, SUBJECT, id, "subject_group", "id", groupId);
        put(client, SUBJECT, id, "subject_group", "name", groupName);
        put(client, SUBJECT, id, "detail", "created_by", CREATED_BY);
    }

    private void seedTaksonomi(HBaseCustomClient client) throws IOException {
        seedTaksonomiOne(client, "TX001", "C1", "Mengingat", SP_TI_ID, SP_TI_NAME);
        seedTaksonomiOne(client, "TX002", "C2", "Memahami", SP_TI_ID, SP_TI_NAME);
        seedTaksonomiOne(client, "TX003", "C3", "Menerapkan", SP_TI_ID, SP_TI_NAME);
        seedTaksonomiOne(client, "TX101", "C1", "Mengingat", SP_SIB_ID, SP_SIB_NAME);
        seedTaksonomiOne(client, "TX102", "C2", "Memahami", SP_SIB_ID, SP_SIB_NAME);
        seedTaksonomiOne(client, "TX103", "C3", "Menerapkan", SP_SIB_ID, SP_SIB_NAME);
    }

    private void seedTaksonomiOne(HBaseCustomClient client, String id, String name, String description, String spId,
            String spName) throws IOException {
        put(client, TAKSONOMI, id, "main", "idTaksonomi", id);
        put(client, TAKSONOMI, id, "main", "namaTaksonomi", name);
        put(client, TAKSONOMI, id, "main", "deskripsiTaksonomi", description);
        studyProgram(client, TAKSONOMI, id, "study_program", spId, spName);
        put(client, TAKSONOMI, id, "detail", "created_by", CREATED_BY);
    }
}
