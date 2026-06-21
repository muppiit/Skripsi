package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class Religion2Seeder extends Seeder2Support {
    private static final TableName TABLE = TableName.valueOf("religions");

    public void seed(HBaseCustomClient client) throws IOException {
        main(client, TABLE, RELIGION_ID, "id", "name", RELIGION_ID, RELIGION_NAME, "Agama Islam");
        main(client, TABLE, "RLG002", "id", "name", "RLG002", "Kristen", "Agama Kristen");
        main(client, TABLE, "RLG003", "id", "name", "RLG003", "Katolik", "Agama Katolik");
        main(client, TABLE, "RLG004", "id", "name", "RLG004", "Hindu", "Agama Hindu");
        main(client, TABLE, "RLG005", "id", "name", "RLG005", "Buddha", "Agama Buddha");
    }
}
