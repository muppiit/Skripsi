package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class AcademicSeeder {

    private static final TableName TABLE_TAKSONOMI = TableName.valueOf("taksonomi");

    public void seed(HBaseCustomClient client) throws IOException {
        seedTaksonomi(client);
    }

    private void seedTaksonomi(HBaseCustomClient client) throws IOException {
        seedTaksonomiOne(client, "TX001", "C1", "Mengingat", "SP001", "Teknik Informatika");
        seedTaksonomiOne(client, "TX002", "C2", "Memahami", "SP001", "Teknik Informatika");
        seedTaksonomiOne(client, "TX003", "C3", "Menerapkan", "SP001", "Teknik Informatika");
        seedTaksonomiOne(client, "TX101", "C1", "Mengingat", "SP002", "Sistem Informasi Bisnis");
        seedTaksonomiOne(client, "TX102", "C2", "Memahami", "SP002", "Sistem Informasi Bisnis");
        seedTaksonomiOne(client, "TX103", "C3", "Menerapkan", "SP002", "Sistem Informasi Bisnis");
    }

    private void seedTaksonomiOne(HBaseCustomClient client, String id, String nama, String deskripsi,
            String studyProgramId, String studyProgramName)
            throws IOException {
        client.insertRecord(TABLE_TAKSONOMI, id, "main", "idTaksonomi", id);
        client.insertRecord(TABLE_TAKSONOMI, id, "main", "namaTaksonomi", nama);
        client.insertRecord(TABLE_TAKSONOMI, id, "main", "deskripsiTaksonomi", deskripsi);
        client.insertRecord(TABLE_TAKSONOMI, id, "study_program", "idSchool", studyProgramId);
        client.insertRecord(TABLE_TAKSONOMI, id, "study_program", "nameSchool", studyProgramName);
        client.insertRecord(TABLE_TAKSONOMI, id, "detail", "created_by", "Seeder");
    }
}
