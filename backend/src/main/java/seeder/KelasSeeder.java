package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class KelasSeeder {

    private static final TableName TABLE_KELAS = TableName.valueOf("kelas");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "KLS001", "1A", "2024", "SP001", "Teknik Informatika");
        seedOne(client, "KLS002", "1B", "2024", "SP001", "Teknik Informatika");
        seedOne(client, "KLS003", "2A", "2023", "SP001", "Teknik Informatika");
        seedOne(client, "KLS004", "2B", "2023", "SP001", "Teknik Informatika");

        seedOne(client, "KLS005", "1A", "2024", "SP002", "Sistem Informasi Bisnis");
        seedOne(client, "KLS006", "1B", "2024", "SP002", "Sistem Informasi Bisnis");
        seedOne(client, "KLS007", "2A", "2023", "SP002", "Sistem Informasi Bisnis");
        seedOne(client, "KLS008", "2B", "2023", "SP002", "Sistem Informasi Bisnis");
    }

    private void seedOne(HBaseCustomClient client, String idKelas, String namaKelas, String angkatan,
            String studyProgramId, String studyProgramName) throws IOException {
        client.insertRecord(TABLE_KELAS, idKelas, "main", "idKelas", idKelas);
        client.insertRecord(TABLE_KELAS, idKelas, "main", "namaKelas", namaKelas);
        client.insertRecord(TABLE_KELAS, idKelas, "angkatan", "value", angkatan);
        client.insertRecord(TABLE_KELAS, idKelas, "study_program", "id", studyProgramId);
        client.insertRecord(TABLE_KELAS, idKelas, "study_program", "name", studyProgramName);
        client.insertRecord(TABLE_KELAS, idKelas, "detail", "created_by", "Seeder");
    }
}
