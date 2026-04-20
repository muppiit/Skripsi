package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class SubjectSeeder {

    private static final TableName TABLE_SUBJECT = TableName.valueOf("subjects");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "SUB001", "Pemrograman Dasar", "Dasar pemrograman", "2", "2024", "SP001",
                "Teknik Informatika");
        seedOne(client, "SUB002", "Algoritma dan Struktur Data", "Struktur data dasar", "3", "2024", "SP001",
                "Teknik Informatika");
        seedOne(client, "SUB003", "Sistem Informasi Manajemen", "Konsep sistem informasi bisnis", "3", "2024",
                "SP002", "Sistem Informasi Bisnis");
        seedOne(client, "SUB004", "Analitik Data Bisnis", "Pengolahan data untuk keputusan bisnis", "2", "2024",
                "SP002", "Sistem Informasi Bisnis");
    }

    private void seedOne(HBaseCustomClient client, String id, String name, String description, String creditPoint,
            String yearCommenced, String studyProgramId, String studyProgramName) throws IOException {
        client.insertRecord(TABLE_SUBJECT, id, "main", "id", id);
        client.insertRecord(TABLE_SUBJECT, id, "main", "name", name);
        client.insertRecord(TABLE_SUBJECT, id, "main", "description", description);
        client.insertRecord(TABLE_SUBJECT, id, "main", "credit_point", creditPoint);
        client.insertRecord(TABLE_SUBJECT, id, "main", "year_commenced", yearCommenced);

        client.insertRecord(TABLE_SUBJECT, id, "study_program", "id", studyProgramId);
        client.insertRecord(TABLE_SUBJECT, id, "study_program", "name", studyProgramName);

        client.insertRecord(TABLE_SUBJECT, id, "subject_group", "id", "SG001");
        client.insertRecord(TABLE_SUBJECT, id, "subject_group", "name", "Mata Kuliah Inti");

        client.insertRecord(TABLE_SUBJECT, id, "detail", "created_by", "Seeder");
    }
}
