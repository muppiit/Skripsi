package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class Student2Seeder extends Seeder2Support {
    private static final TableName TABLE = TableName.valueOf("students");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "STU001", "USR008", "2351507001", "Andi Pratama", "Laki-laki", "0811111111",
                "2005-01-10", "Lumajang", "Jl. Melati 1", SP_TI_ID, SP_TI_NAME, "KLS001", "1A", "THN001",
                "2025/2026");
        seedOne(client, "STU002", "USR009", "2351507002", "Citra Lestari", "Perempuan", "0811111112",
                "2005-09-05", "Jember", "Jl. Kenanga 5", SP_TI_ID, SP_TI_NAME, "KLS001", "1A", "THN001",
                "2025/2026");
        seedOne(client, "STU003", "USR010", "2351508001", "Dian Puspita", "Perempuan", "0811111113",
                "2005-02-14", "Malang", "Jl. Anggrek 8", SP_SIB_ID, SP_SIB_NAME, "KLS003", "1A", "THN002",
                "2025/2026");
        seedOne(client, "STU004", "USR011", "2351508002", "Galih Maulana", "Laki-laki", "0811111114",
                "2005-05-08", "Probolinggo", "Jl. Mawar 9", SP_SIB_ID, SP_SIB_NAME, "KLS003", "1A", "THN002",
                "2025/2026");
    }

    private void seedOne(HBaseCustomClient client, String id, String userId, String nim, String name, String gender,
            String phone, String birthDate, String placeBorn, String address, String spId, String spName,
            String kelasId, String kelasName, String tahunId, String tahunName) throws IOException {
        put(client, TABLE, id, "main", "id", id);
        put(client, TABLE, id, "main", "user_id", userId);
        put(client, TABLE, id, "main", "nisn", nim);
        put(client, TABLE, id, "main", "name", name);
        put(client, TABLE, id, "main", "gender", gender);
        put(client, TABLE, id, "main", "phone", phone);
        put(client, TABLE, id, "main", "birth_date", birthDate);
        put(client, TABLE, id, "main", "place_born", placeBorn);
        put(client, TABLE, id, "main", "address", address);
        religion(client, TABLE, id);
        studyProgram(client, TABLE, id, "study_program", spId, spName);
        put(client, TABLE, id, "kelas", "idKelas", kelasId);
        put(client, TABLE, id, "kelas", "namaKelas", kelasName);
        put(client, TABLE, id, "tahunAjaran", "idTahun", tahunId);
        put(client, TABLE, id, "tahunAjaran", "tahunAjaran", tahunName);
        put(client, TABLE, id, "detail", "created_by", CREATED_BY);
    }
}
