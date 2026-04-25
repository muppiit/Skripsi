package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class KelasSeeder {

    private static final TableName TABLE_KELAS = TableName.valueOf("kelas");

    public void seed(HBaseCustomClient client) throws IOException {
        // THN001 = "2024/2025" (dari TahunAjaranSeeder)
        seedOne(client, "KLS001", "1A", "SP001", "Teknik Informatika", "THN001", "2024/2025");
        seedOne(client, "KLS002", "1B", "SP001", "Teknik Informatika", "THN001", "2024/2025");
        seedOne(client, "KLS003", "2A", "SP001", "Teknik Informatika", "THN002", "2024/2025");
        seedOne(client, "KLS004", "2B", "SP001", "Teknik Informatika", "THN002", "2024/2025");

        seedOne(client, "KLS005", "1A", "SP002", "Sistem Informasi Bisnis", "THN001", "2024/2025");
        seedOne(client, "KLS006", "1B", "SP002", "Sistem Informasi Bisnis", "THN001", "2024/2025");
        seedOne(client, "KLS007", "2A", "SP002", "Sistem Informasi Bisnis", "THN002", "2024/2025");
        seedOne(client, "KLS008", "2B", "SP002", "Sistem Informasi Bisnis", "THN002", "2024/2025");
    }

    private void seedOne(HBaseCustomClient client, String idKelas, String namaKelas,
            String studyProgramId, String studyProgramName,
            String idTahunAjaran, String namaTahunAjaran) throws IOException {
        client.insertRecord(TABLE_KELAS, idKelas, "main", "idKelas", idKelas);
        client.insertRecord(TABLE_KELAS, idKelas, "main", "namaKelas", namaKelas);
        client.insertRecord(TABLE_KELAS, idKelas, "study_program", "id", studyProgramId);
        client.insertRecord(TABLE_KELAS, idKelas, "study_program", "name", studyProgramName);
        client.insertRecord(TABLE_KELAS, idKelas, "tahunAjaran", "idTahun", idTahunAjaran);
        client.insertRecord(TABLE_KELAS, idKelas, "tahunAjaran", "tahunAjaran", namaTahunAjaran);
        client.insertRecord(TABLE_KELAS, idKelas, "detail", "created_by", "Seeder");
    }
}
