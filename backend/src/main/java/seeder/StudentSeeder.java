package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class StudentSeeder {

    private static final TableName TABLE_STUDENT = TableName.valueOf("students");

    public void seed(HBaseCustomClient client) throws IOException {
        // THN001 = "2024/2025" (dari TahunAjaranSeeder)
        seedOne(client, "STU001", "USR005", "NISN000001", "Andi Pratama", "L", "0811111111", "2008-01-10",
                "Lumajang", "Jl. Melati 1", "R001", "Islam", "SP001", "Teknik Informatika", "KLS001",
                "1A", "THN001", "2024/2025", new String[] { "SEA001", "SEA002" });
        seedOne(client, "STU002", "USR006", "NISN000002", "Budi Santoso", "L", "0811111112", "2008-04-21",
                "Jember", "Jl. Melati 2", "R001", "Islam", "SP001", "Teknik Informatika", "KLS001",
                "1A", "THN001", "2024/2025", new String[] { "SEA001" });
        seedOne(client, "STU003", "USR007", "NISN000003", "Citra Lestari", "P", "0811111113", "2007-09-05",
                "Probolinggo", "Jl. Kenanga 5", "R001", "Islam", "SP001", "Teknik Informatika",
                "KLS003", "2A", "THN002", "2024/2025", new String[] { "SEA003" });
        seedOne(client, "STU004", "USR008", "NISN000004", "Dian Puspita", "P", "0811111114", "2008-02-14",
                "Malang", "Jl. Anggrek 8", "R001", "Islam", "SP002", "Sistem Informasi Bisnis",
                "KLS005", "1A", "THN001", "2024/2025", new String[] { "SEA004" });
        seedOne(client, "STU005", "USR012", "NISN000005", "Eka Saputra", "L", "0811111115", "2008-03-16",
                "Lumajang", "Jl. Flamboyan 3", "R001", "Islam", "SP001", "Teknik Informatika",
                "KLS002", "1B", "THN001", "2024/2025", new String[] { "SEA002" });
        seedOne(client, "STU006", "USR013", "NISN000006", "Fitri Rahma", "P", "0811111116", "2008-06-11",
                "Jember", "Jl. Cempaka 7", "R001", "Islam", "SP001", "Teknik Informatika",
                "KLS004", "2B", "THN002", "2024/2025", new String[] { "SEA003" });
        seedOne(client, "STU007", "USR014", "NISN000007", "Galih Maulana", "L", "0811111117", "2008-05-08",
                "Probolinggo", "Jl. Mawar 9", "R001", "Islam", "SP002", "Sistem Informasi Bisnis",
                "KLS006", "1B", "THN001", "2024/2025", new String[] { "SEA004" });
        seedOne(client, "STU008", "USR015", "NISN000008", "Hana Oktaviani", "P", "0811111118", "2007-12-02",
                "Pasuruan", "Jl. Dahlia 10", "R001", "Islam", "SP002", "Sistem Informasi Bisnis",
                "KLS008", "2B", "THN002", "2024/2025", new String[] { "SEA005" });
    }

    private void seedOne(HBaseCustomClient client, String id, String userId, String nisn, String name,
            String gender, String phone, String birthDate, String placeBorn, String address,
            String religionId, String religionName, String studyProgramId, String studyProgramName,
            String kelasId, String kelasName,
            String idTahunAjaran, String namaTahunAjaran,
            String[] seasonIds) throws IOException {

        client.insertRecord(TABLE_STUDENT, id, "main", "id", id);
        client.insertRecord(TABLE_STUDENT, id, "main", "user_id", userId);
        client.insertRecord(TABLE_STUDENT, id, "main", "nisn", nisn);
        client.insertRecord(TABLE_STUDENT, id, "main", "name", name);
        client.insertRecord(TABLE_STUDENT, id, "main", "gender", gender);
        client.insertRecord(TABLE_STUDENT, id, "main", "phone", phone);
        client.insertRecord(TABLE_STUDENT, id, "main", "birth_date", birthDate);
        client.insertRecord(TABLE_STUDENT, id, "main", "place_born", placeBorn);
        client.insertRecord(TABLE_STUDENT, id, "main", "address", address);

        client.insertRecord(TABLE_STUDENT, id, "religion", "id", religionId);
        client.insertRecord(TABLE_STUDENT, id, "religion", "name", religionName);

        client.insertRecord(TABLE_STUDENT, id, "study_program", "id", studyProgramId);
        client.insertRecord(TABLE_STUDENT, id, "study_program", "name", studyProgramName);

        client.insertRecord(TABLE_STUDENT, id, "kelas", "idKelas", kelasId);
        client.insertRecord(TABLE_STUDENT, id, "kelas", "namaKelas", kelasName);

        client.insertRecord(TABLE_STUDENT, id, "tahunAjaran", "idTahun", idTahunAjaran);
        client.insertRecord(TABLE_STUDENT, id, "tahunAjaran", "tahunAjaran", namaTahunAjaran);

        for (int i = 0; i < seasonIds.length; i++) {
            client.insertRecord(TABLE_STUDENT, id, "detail", "season_" + i, seasonIds[i]);
        }
        client.insertRecord(TABLE_STUDENT, id, "detail", "created_by", "Seeder");
    }
}
