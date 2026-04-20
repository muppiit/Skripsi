package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class SubjectGroupSeeder {

    private static final TableName TABLE_SUBJECT_GROUP = TableName.valueOf("subject_groups");

    public void seed(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_SUBJECT_GROUP, "SG001", "main", "id", "SG001");
        client.insertRecord(TABLE_SUBJECT_GROUP, "SG001", "main", "name", "Mata Kuliah Inti");
        client.insertRecord(TABLE_SUBJECT_GROUP, "SG001", "main", "description", "Kelompok mata kuliah inti");
        client.insertRecord(TABLE_SUBJECT_GROUP, "SG001", "detail", "created_by", "Seeder");
    }
}
