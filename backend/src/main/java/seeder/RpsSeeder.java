package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class RpsSeeder {

    private static final TableName TABLE_RPS = TableName.valueOf("rps");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "RPS001", "Pemrograman Dasar", "2", "1", "CP1", "CP1-1", "SUB001", "SP001",
                "Teknik Informatika");
        seedOne(client, "RPS002", "Algoritma dan Struktur Data", "3", "2", "CP2", "CP2-1", "SUB002", "SP001",
                "Teknik Informatika");
        seedOne(client, "RPS003", "Praktikum Pemrograman", "2", "3", "CP3", "CP3-1", "SUB001", "SP001",
                "Teknik Informatika");
        seedOne(client, "RPS004", "Pemrograman Berorientasi Objek", "3", "4", "CP4", "CP4-1", "SUB002",
                "SP001", "Teknik Informatika");

        seedOne(client, "RPS005", "Sistem Informasi Manajemen", "3", "1", "CP5", "CP5-1", "SUB003", "SP002",
                "Sistem Informasi Bisnis");
        seedOne(client, "RPS006", "Analitik Data Bisnis", "2", "2", "CP6", "CP6-1", "SUB004", "SP002",
                "Sistem Informasi Bisnis");
        seedOne(client, "RPS007", "Perancangan Proses Bisnis", "3", "3", "CP7", "CP7-1", "SUB003", "SP002",
                "Sistem Informasi Bisnis");
        seedOne(client, "RPS008", "Visualisasi Data", "2", "4", "CP8", "CP8-1", "SUB004", "SP002",
                "Sistem Informasi Bisnis");
    }

    private void seedOne(HBaseCustomClient client, String id, String name, String sks, String semester,
            String cplProdi, String cplMk, String subjectId, String studyProgramId, String studyProgramName)
            throws IOException {
        client.insertRecord(TABLE_RPS, id, "main", "id", id);
        client.insertRecord(TABLE_RPS, id, "main", "name", name);
        client.insertRecord(TABLE_RPS, id, "main", "sks", sks);
        client.insertRecord(TABLE_RPS, id, "main", "semester", semester);
        client.insertRecord(TABLE_RPS, id, "main", "cpl_prodi", cplProdi);
        client.insertRecord(TABLE_RPS, id, "main", "cpl_mk", cplMk);

        client.insertRecord(TABLE_RPS, id, "learning_media_softwares", "soft_0", "{\"id\":\"LM001\",\"name\":\"LMS\"}");
        client.insertRecord(TABLE_RPS, id, "learning_media_hardwares", "hard_0",
                "{\"id\":\"LM002\",\"name\":\"Projector\"}");

        client.insertRecord(TABLE_RPS, id, "study_program", "id", studyProgramId);
        client.insertRecord(TABLE_RPS, id, "study_program", "name", studyProgramName);

        client.insertRecord(TABLE_RPS, id, "subject", "id", subjectId);
        client.insertRecord(TABLE_RPS, id, "subject", "name", name);

        client.insertRecord(TABLE_RPS, id, "dev_lecturers", "lecture_0",
                "{\"id\":\"LEC001\",\"name\":\"Dosen Pengampu\"}");
        client.insertRecord(TABLE_RPS, id, "teaching_lecturers", "lecture_0",
                "{\"id\":\"LEC001\",\"name\":\"Dosen Pengampu\"}");
        client.insertRecord(TABLE_RPS, id, "coordinator_lecturers", "lecture_0",
                "{\"id\":\"LEC001\",\"name\":\"Dosen Pengampu\"}");

        client.insertRecord(TABLE_RPS, id, "ka_study_program", "id", "LEC001");
        client.insertRecord(TABLE_RPS, id, "ka_study_program", "name", "Dosen Pengampu");

        client.insertRecord(TABLE_RPS, id, "detail", "created_by", "Seeder");
    }
}
