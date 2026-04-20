package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class TahunAjaranSeeder {

    private static final TableName TABLE_TAHUN = TableName.valueOf("tahunAjaran");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "THN001", "2024/2025", "SP001", "Teknik Informatika");
        seedOne(client, "THN002", "2024/2025", "SP002", "Sistem Informasi Bisnis");
    }

    private void seedOne(HBaseCustomClient client, String idTahun, String tahunAjaran,
            String studyProgramId, String studyProgramName) throws IOException {
        client.insertRecord(TABLE_TAHUN, idTahun, "main", "idTahun", idTahun);
        client.insertRecord(TABLE_TAHUN, idTahun, "main", "tahunAjaran", tahunAjaran);
        client.insertRecord(TABLE_TAHUN, idTahun, "study_program", "idSchool", studyProgramId);
        client.insertRecord(TABLE_TAHUN, idTahun, "study_program", "nameSchool", studyProgramName);
        client.insertRecord(TABLE_TAHUN, idTahun, "detail", "created_by", "Seeder");
    }
}
