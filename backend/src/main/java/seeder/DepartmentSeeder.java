package seeder;

import create_database_structure.HBaseCustomClient;
import org.apache.hadoop.hbase.TableName;

import java.io.IOException;

public class DepartmentSeeder {

    private static final TableName TABLE_DEPARTMENT = TableName.valueOf("departments");

    public void seed(HBaseCustomClient client) throws IOException {
        client.insertRecord(TABLE_DEPARTMENT, "DPT001", "main", "id", "DPT001");
        client.insertRecord(TABLE_DEPARTMENT, "DPT001", "main", "name", "Teknologi Informasi");
        client.insertRecord(TABLE_DEPARTMENT, "DPT001", "main", "description", "Departemen Teknologi Informasi");
        client.insertRecord(TABLE_DEPARTMENT, "DPT001", "detail", "created_by", "Seeder");
    }
}
