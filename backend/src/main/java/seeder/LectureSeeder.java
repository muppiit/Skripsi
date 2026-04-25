package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class LectureSeeder {

    private static final TableName TABLE_LECTURE = TableName.valueOf("lectures");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "LEC001", "19880001", "Dosen TI Polinema Lumajang", "Lumajang", "1988-01-01", "L",
                "Aktif", "081234567890", "dosen.ti@polinema.ac.id", "Jl. Raya Kampus No. 1", "SP001",
                "Teknik Informatika");

        seedOne(client, "LEC002", "19890002", "Ratna Wulandari", "Jember", "1989-05-12", "P", "Aktif",
                "081234567891", "ratna.wulandari@polinema.ac.id", "Jl. Letkol Istiqlah No. 12", "SP001",
                "Teknik Informatika");

        seedOne(client, "LEC003", "19900003", "Fajar Hidayat", "Malang", "1990-09-25", "L", "Aktif",
                "081234567892", "fajar.hidayat@polinema.ac.id", "Jl. Diponegoro No. 5", "SP002",
                "Sistem Informasi Bisnis");

        seedOne(client, "LEC004", "19910004", "Sari Ayu", "Lumajang", "1991-02-18", "P", "Aktif",
                "081234567893", "sari.ayu@polinema.ac.id", "Jl. WR Supratman No. 8", "SP002",
                "Sistem Informasi Bisnis");
    }

    private void seedOne(HBaseCustomClient client, String id, String nip, String name, String placeBorn,
            String dateBorn, String gender, String status, String phone, String email, String address,
            String studyProgramId, String studyProgramName) throws IOException {
        client.insertRecord(TABLE_LECTURE, id, "main", "id", id);
        client.insertRecord(TABLE_LECTURE, id, "main", "nip", nip);
        client.insertRecord(TABLE_LECTURE, id, "main", "name", name);
        client.insertRecord(TABLE_LECTURE, id, "main", "place_born", placeBorn);
        client.insertRecord(TABLE_LECTURE, id, "main", "date_born", dateBorn);
        client.insertRecord(TABLE_LECTURE, id, "main", "gender", gender);
        client.insertRecord(TABLE_LECTURE, id, "main", "status", status);
        client.insertRecord(TABLE_LECTURE, id, "main", "phone", phone);
        client.insertRecord(TABLE_LECTURE, id, "main", "email", email);
        client.insertRecord(TABLE_LECTURE, id, "main", "address", address);
        client.insertRecord(TABLE_LECTURE, id, "religion", "id", "R001");
        client.insertRecord(TABLE_LECTURE, id, "religion", "name", "Islam");
        client.insertRecord(TABLE_LECTURE, id, "study_program", "id", studyProgramId);
        client.insertRecord(TABLE_LECTURE, id, "study_program", "name", studyProgramName);
        client.insertRecord(TABLE_LECTURE, id, "detail", "created_by", "Seeder");
    }
}
