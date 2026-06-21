package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class Department2Seeder extends Seeder2Support {
    private static final TableName TABLE = TableName.valueOf("departments");

    public void seed(HBaseCustomClient client) throws IOException {
        main(client, TABLE, DEPARTMENT_ID, "id", "name", DEPARTMENT_ID, DEPARTMENT_NAME,
                "Jurusan " + DEPARTMENT_NAME);
    }
}
