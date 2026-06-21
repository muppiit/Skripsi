package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class StudyProgram2Seeder extends Seeder2Support {
    private static final TableName TABLE = TableName.valueOf("study_programs");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, SP_TI_ID, SP_TI_NAME);
        seedOne(client, SP_SIB_ID, SP_SIB_NAME);
    }

    private void seedOne(HBaseCustomClient client, String id, String name) throws IOException {
        main(client, TABLE, id, "id", "name", id, name, "Program studi " + name);
        put(client, TABLE, id, "department", "id", DEPARTMENT_ID);
        put(client, TABLE, id, "department", "name", DEPARTMENT_NAME);
    }
}
