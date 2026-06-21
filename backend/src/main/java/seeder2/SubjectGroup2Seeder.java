package seeder2;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class SubjectGroup2Seeder extends Seeder2Support {
    private static final TableName TABLE = TableName.valueOf("subject_groups");

    public void seed(HBaseCustomClient client) throws IOException {
        main(client, TABLE, "SG001", "id", "name", "SG001", "Mata Kuliah Dasar",
                "Rumpun mata kuliah dasar program studi");
        main(client, TABLE, "SG002", "id", "name", "SG002", "Mata Kuliah Keahlian",
                "Rumpun mata kuliah keahlian program studi");
    }
}
