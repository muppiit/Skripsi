package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;
import java.time.Instant;

abstract class Seeder2Support {
    static final String CREATED_BY = "Seeder2";
    static final String PASSWORD_HASH = "$2a$10$SDRWMUk.2fnli0GTmqodJexjRksTw0En98dU8fdKsw7nTbZzMrj.2";

    static final String DEPARTMENT_ID = "DPT001";
    static final String DEPARTMENT_NAME = "Tekhnologi Informasi";

    static final String SP_TI_ID = "SP001";
    static final String SP_TI_NAME = "Teknik Informatika";
    static final String SP_SIB_ID = "SP002";
    static final String SP_SIB_NAME = "Sistem Informasi Bisnis";

    static final String RELIGION_ID = "RLG001";
    static final String RELIGION_NAME = "Islam";

    void put(HBaseCustomClient client, TableName table, String row, String family, String qualifier, String value)
            throws IOException {
        if (value != null) {
            client.insertRecord(table, row, family, qualifier, value);
        }
    }

    void main(HBaseCustomClient client, TableName table, String row, String idQualifier, String nameQualifier,
            String id, String name, String description) throws IOException {
        put(client, table, row, "main", idQualifier, id);
        put(client, table, row, "main", nameQualifier, name);
        put(client, table, row, "main", "description", description);
        put(client, table, row, "detail", "created_by", CREATED_BY);
        put(client, table, row, "detail", "created_at", Instant.now().toString());
    }

    void studyProgram(HBaseCustomClient client, TableName table, String row, String family, String id, String name)
            throws IOException {
        put(client, table, row, family, "id", id);
        put(client, table, row, family, "name", name);
        put(client, table, row, family, "idSchool", id);
        put(client, table, row, family, "nameSchool", name);
    }

    void religion(HBaseCustomClient client, TableName table, String row) throws IOException {
        put(client, table, row, "religion", "id", RELIGION_ID);
        put(client, table, row, "religion", "name", RELIGION_NAME);
    }

    String json(String id, String name) {
        return "{\"id\":\"" + id + "\",\"name\":\"" + name + "\"}";
    }
}
