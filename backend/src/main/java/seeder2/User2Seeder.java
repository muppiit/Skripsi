package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;
import java.time.Instant;

public class User2Seeder extends Seeder2Support {
    private static final TableName TABLE = TableName.valueOf("users");

    public void seed(HBaseCustomClient client) throws IOException {
        seedUser(client, "USR001", "Administrator", "admin", "admin@kampus.ac.id", "1", null, null);
        seedUser(client, "USR002", "Operator Teknik Informatika", "operator1", "operator.ti@kampus.ac.id", "2",
                SP_TI_ID, SP_TI_NAME);
        seedUser(client, "USR003", "Operator Sistem Informasi Bisnis", "operator2", "operator.sib@kampus.ac.id",
                "2", SP_SIB_ID, SP_SIB_NAME);
        seedUser(client, "USR004", "Budi Raharjo", "dosen_ti_lmj", "budi.raharjo@kampus.ac.id", "3", SP_TI_ID,
                SP_TI_NAME);
        seedUser(client, "USR005", "Ratna Wulandari", "ratna_wulandari", "ratna.wulandari@kampus.ac.id", "3",
                SP_TI_ID, SP_TI_NAME);
        seedUser(client, "USR006", "Fajar Hidayat", "fajar_hidayat", "fajar.hidayat@kampus.ac.id", "3", SP_SIB_ID,
                SP_SIB_NAME);
        seedUser(client, "USR007", "Sari Ayu", "sari_ayu", "sari.ayu@kampus.ac.id", "3", SP_SIB_ID, SP_SIB_NAME);
        seedUser(client, "USR008", "Andi Pratama", "andi_pratama", "andi.pratama@student.kampus.ac.id", "5",
                SP_TI_ID, SP_TI_NAME);
        seedUser(client, "USR009", "Citra Lestari", "citra_lestari", "citra.lestari@student.kampus.ac.id", "5",
                SP_TI_ID, SP_TI_NAME);
        seedUser(client, "USR010", "Dian Puspita", "dian_puspita", "dian.puspita@student.kampus.ac.id", "5",
                SP_SIB_ID, SP_SIB_NAME);
        seedUser(client, "USR011", "Galih Maulana", "galih_maulana", "galih.maulana@student.kampus.ac.id", "5",
                SP_SIB_ID, SP_SIB_NAME);
    }

    private void seedUser(HBaseCustomClient client, String id, String name, String username, String email, String role,
            String spId, String spName) throws IOException {
        client.deleteRecord(TABLE.toString(), id);
        put(client, TABLE, id, "main", "id", id);
        put(client, TABLE, id, "main", "name", name);
        put(client, TABLE, id, "main", "username", username);
        put(client, TABLE, id, "main", "email", email);
        put(client, TABLE, id, "main", "password", PASSWORD_HASH);
        put(client, TABLE, id, "main", "roles", role);
        put(client, TABLE, id, "main", "created_at", Instant.now().toString());
        if (spId != null) {
            put(client, TABLE, id, "study_program", "idSchool", spId);
            put(client, TABLE, id, "study_program", "nameSchool", spName);
        }
        put(client, TABLE, id, "detail", "created_by", CREATED_BY);
    }
}
