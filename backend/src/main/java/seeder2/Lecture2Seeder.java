package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class Lecture2Seeder extends Seeder2Support {
    private static final TableName TABLE = TableName.valueOf("lectures");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "LEC001", "USR004", "19880001", "Budi Raharjo", "Lumajang", "1988-01-01", "Laki-laki",
                "081234567890", "Jl. Kampus No. 1", SP_TI_ID, SP_TI_NAME);
        seedOne(client, "LEC002", "USR005", "19890002", "Ratna Wulandari", "Jember", "1989-05-12", "Perempuan",
                "081234567891", "Jl. Merdeka No. 12", SP_TI_ID, SP_TI_NAME);
        seedOne(client, "LEC003", "USR006", "19900003", "Fajar Hidayat", "Malang", "1990-09-25", "Laki-laki",
                "081234567892", "Jl. Diponegoro No. 5", SP_SIB_ID, SP_SIB_NAME);
        seedOne(client, "LEC004", "USR007", "19910004", "Sari Ayu", "Lumajang", "1991-02-18", "Perempuan",
                "081234567893", "Jl. Supratman No. 8", SP_SIB_ID, SP_SIB_NAME);
    }

    private void seedOne(HBaseCustomClient client, String id, String userId, String nip, String name, String placeBorn,
            String dateBorn, String gender, String phone, String address, String spId, String spName)
            throws IOException {
        put(client, TABLE, id, "main", "id", id);
        put(client, TABLE, id, "main", "user_id", userId);
        put(client, TABLE, id, "main", "nip", nip);
        put(client, TABLE, id, "main", "name", name);
        put(client, TABLE, id, "main", "place_born", placeBorn);
        put(client, TABLE, id, "main", "date_born", dateBorn);
        put(client, TABLE, id, "main", "gender", gender);
        put(client, TABLE, id, "main", "status", "Aktif");
        put(client, TABLE, id, "main", "phone", phone);
        put(client, TABLE, id, "main", "address", address);
        religion(client, TABLE, id);
        studyProgram(client, TABLE, id, "study_program", spId, spName);
        put(client, TABLE, id, "detail", "created_by", CREATED_BY);
    }
}
