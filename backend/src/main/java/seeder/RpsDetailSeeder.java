package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class RpsDetailSeeder {

    private static final TableName TABLE_RPS_DETAIL = TableName.valueOf("rps_details");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "RPD001-1", "1", "Minggu 1", "RPS001", "Pemrograman Dasar", "CP MK 1", "20");
        seedOne(client, "RPD002-2", "2", "Minggu 2", "RPS002", "Algoritma dan Struktur Data", "CP MK 2", "25");
        seedOne(client, "RPD003-3", "3", "Minggu 3", "RPS003", "Praktikum Pemrograman", "CP MK 3", "25");
        seedOne(client, "RPD004-4", "4", "Minggu 4", "RPS004", "Pemrograman Berorientasi Objek", "CP MK 4", "30");
        seedOne(client, "RPD005-1", "1", "Minggu 1", "RPS005", "Sistem Informasi Manajemen", "CP MK 5", "20");
        seedOne(client, "RPD006-2", "2", "Minggu 2", "RPS006", "Analitik Data Bisnis", "CP MK 6", "25");
        seedOne(client, "RPD007-3", "3", "Minggu 3", "RPS007", "Perancangan Proses Bisnis", "CP MK 7", "25");
        seedOne(client, "RPD008-4", "4", "Minggu 4", "RPS008", "Visualisasi Data", "CP MK 8", "30");
    }

    private void seedOne(HBaseCustomClient client, String id, String week, String weekLabel, String rpsId,
            String rpsName, String subCpMk, String weight) throws IOException {
        client.insertRecord(TABLE_RPS_DETAIL, id, "main", "id", id);
        client.insertRecord(TABLE_RPS_DETAIL, id, "main", "week", week);
        client.insertRecord(TABLE_RPS_DETAIL, id, "main", "weekLabel", weekLabel);
        client.insertRecord(TABLE_RPS_DETAIL, id, "main", "sub_cp_mk", subCpMk);
        client.insertRecord(TABLE_RPS_DETAIL, id, "main", "weight", weight);

        client.insertRecord(TABLE_RPS_DETAIL, id, "rps", "id", rpsId);
        client.insertRecord(TABLE_RPS_DETAIL, id, "rps", "name", rpsName);

        client.insertRecord(TABLE_RPS_DETAIL, id, "learning_materials", "lm_0", "Materi " + subCpMk);
        client.insertRecord(TABLE_RPS_DETAIL, id, "form_learning", "id", "FL001");
        client.insertRecord(TABLE_RPS_DETAIL, id, "form_learning", "name", "Tatap Muka");
        client.insertRecord(TABLE_RPS_DETAIL, id, "learning_methods", "lm_0",
                "{\"id\":\"LMTH001\",\"name\":\"Project Based Learning\"}");
        client.insertRecord(TABLE_RPS_DETAIL, id, "assignments", "lm_0", "Tugas " + subCpMk);
        client.insertRecord(TABLE_RPS_DETAIL, id, "estimated_times", "et_0", "2 x 50 menit");
        client.insertRecord(TABLE_RPS_DETAIL, id, "student_learning_experiences", "sle_0",
                "Praktik dan diskusi");
        client.insertRecord(TABLE_RPS_DETAIL, id, "assessment_criterias", "ac_0",
                "{\"id\":\"AC001\",\"name\":\"Kelengkapan Solusi\"}");
        client.insertRecord(TABLE_RPS_DETAIL, id, "appraisal_forms", "af_0",
                "{\"id\":\"AF001\",\"name\":\"Rubrik Penilaian\"}");
        client.insertRecord(TABLE_RPS_DETAIL, id, "assessment_indicators", "ai_0",
                "Mahasiswa mampu mencapai " + subCpMk);

        client.insertRecord(TABLE_RPS_DETAIL, id, "detail", "created_by", "Seeder");
    }
}
