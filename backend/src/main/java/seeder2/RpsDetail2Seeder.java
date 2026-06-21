package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class RpsDetail2Seeder extends Seeder2Support {
    private static final TableName TABLE = TableName.valueOf("rps_details");

    public void seed(HBaseCustomClient client) throws IOException {
        seedRpsDetails(client, "RPS001", "Pemrograman Dasar", "RPD001", "Dasar algoritma dan pemrograman");
        seedRpsDetails(client, "RPS002", "Algoritma dan Struktur Data", "RPD002", "Struktur kontrol dan array");
        seedRpsDetails(client, "RPS003", "Sistem Informasi Manajemen", "RPD003", "Konsep sistem informasi");
        seedRpsDetails(client, "RPS004", "Analitik Data Bisnis", "RPD004", "Dasar analitik data");
    }

    private void seedRpsDetails(HBaseCustomClient client, String rpsId, String rpsName, String prefix, String material)
            throws IOException {
        seedOne(client, prefix + "-1", "1", "Minggu 1", rpsId, rpsName, "Sub-CPMK 1 " + rpsName, material,
                "25");
        seedOne(client, prefix + "-2", "2", "Minggu 2", rpsId, rpsName, "Sub-CPMK 2 " + rpsName,
                "Latihan dan studi kasus " + rpsName, "25");
        seedOne(client, prefix + "-3", "3", "Minggu 3", rpsId, rpsName, "Sub-CPMK 3 " + rpsName,
                "Praktik terarah " + rpsName, "25");
        seedOne(client, prefix + "-4", "4", "Minggu 4", rpsId, rpsName, "Sub-CPMK 4 " + rpsName,
                "Evaluasi capaian " + rpsName, "25");
    }

    private void seedOne(HBaseCustomClient client, String id, String week, String weekLabel, String rpsId,
            String rpsName, String subCpMk, String material, String weight) throws IOException {
        put(client, TABLE, id, "main", "id", id);
        put(client, TABLE, id, "main", "week", week);
        put(client, TABLE, id, "main", "weekLabel", weekLabel);
        put(client, TABLE, id, "main", "sub_cp_mk", subCpMk);
        put(client, TABLE, id, "main", "weight", weight);
        put(client, TABLE, id, "rps", "id", rpsId);
        put(client, TABLE, id, "rps", "name", rpsName);
        put(client, TABLE, id, "learning_materials", "lm_0", material);
        put(client, TABLE, id, "form_learning", "id", "FL001");
        put(client, TABLE, id, "form_learning", "name", "Tatap Muka");
        put(client, TABLE, id, "learning_methods", "lm_0", json("LMTH001", "Project Based Learning"));
        put(client, TABLE, id, "assignments", "asg_0", "Tugas " + weekLabel + " - " + rpsName);
        put(client, TABLE, id, "estimated_times", "et_0", "2 x 50 menit");
        put(client, TABLE, id, "student_learning_experiences", "sle_0", "Diskusi, praktik, dan refleksi");
        put(client, TABLE, id, "assessment_criterias", "ac_0", json("AC001", "Ketepatan Jawaban"));
        put(client, TABLE, id, "assessment_criterias", "ac_1", json("AC002", "Kelengkapan Solusi"));
        put(client, TABLE, id, "appraisal_forms", "af_0", json("AF001", "Tes Tulis"));
        put(client, TABLE, id, "assessment_indicators", "ai_0", "Mahasiswa mampu mencapai " + subCpMk);
        put(client, TABLE, id, "detail", "created_by", CREATED_BY);
    }
}
