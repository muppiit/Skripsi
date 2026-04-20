package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class StudyProgramSeeder {

    private static final TableName TABLE_STUDY_PROGRAM = TableName.valueOf("study_programs");

    public void seed(HBaseCustomClient client) throws IOException {
        seedOne(client, "SP001", "Teknik Informatika");
        seedOne(client, "SP002", "Sistem Informasi Bisnis");
    }

    private void seedOne(HBaseCustomClient client, String id, String name) throws IOException {
        client.insertRecord(TABLE_STUDY_PROGRAM, id, "main", "id", id);
        client.insertRecord(TABLE_STUDY_PROGRAM, id, "main", "name", name);
        client.insertRecord(TABLE_STUDY_PROGRAM, id, "department", "id", "DPT001");
        client.insertRecord(TABLE_STUDY_PROGRAM, id, "department", "name", "Teknologi Informasi");
        client.insertRecord(TABLE_STUDY_PROGRAM, id, "detail", "created_by", "Seeder");
    }
}
